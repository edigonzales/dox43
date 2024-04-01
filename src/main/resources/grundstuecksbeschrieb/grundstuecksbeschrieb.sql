WITH grundstueck AS 
(
    SELECT 
        nummer,
        art_txt AS art,
        flaechenmass,
        gemeinde,
        grundbuch,
        nbident,
        egrid,
        geometrie
    FROM 
        agi_mopublic_pub.mopublic_grundstueck AS g
    WHERE 
        ST_Intersects(ST_SetSRID(ST_MakePoint(CAST(:x AS integer), CAST(:y AS integer)), 2056), g.geometrie)
)
,
bodenbedeckung AS 
(
    SELECT 
        art_txt, 
        --b.geometrie,
        ST_Area((ST_Dump(ST_CollectionExtract(ST_Intersection(grundstueck.geometrie, b.geometrie), 3))).geom) AS geom,
        grundstueck.egrid
    FROM 
        agi_mopublic_pub.mopublic_bodenbedeckung AS b
        LEFT JOIN grundstueck 
        ON ST_Intersects(grundstueck.geometrie, b.geometrie)
    WHERE
        ST_Intersects(b.geometrie, grundstueck.geometrie)
)
,
bodbdanteile AS 
(
    SELECT 
        art_txt,
        sum(geom) AS flaechenmass,
        egrid
    FROM 
        bodenbedeckung
    GROUP BY 
        egrid, art_txt
)
,
bodbdanteil_xml AS 
(
    SELECT
        xmlagg(
            xmlelement(name bodbdanteil, 
                xmlelement(name art, art_txt), 
                xmlelement(name artbezeichnung, art_txt),
                xmlelement(name flaechenmass, flaechenmass)
            )    
        ) AS bodbdanteil_xmlelement,
        bodbdanteile.egrid
    FROM    
        bodbdanteile
    GROUP BY 
        egrid
)
,
flurnamen_xml AS 
(
    SELECT 
        xmlagg(
            xmlelement(name flurname, flurname)
        ) AS flurnamen_xmlelement,
        grundstueck.egrid
    FROM 
        agi_mopublic_pub.mopublic_flurname AS f 
        RIGHT JOIN grundstueck 
        ON ST_Intersects(grundstueck.geometrie, f.geometrie)
    WHERE 
      ST_Area(ST_Intersection(grundstueck.geometrie, f.geometrie)) > 1
    GROUP BY
        egrid    
)
,
pre_result AS (
    SELECT DISTINCT ON (grundstueck.egrid)
        xmlagg(
            xmlelement(name grundstueck, 
                    xmlelement(name nummer, grundstueck.nummer),
                    xmlelement(name gemeinde, grundstueck.gemeinde),
                    xmlelement(name grundbuch, grundstueck.grundbuch),
                    xmlelement(name egrid, grundstueck.egrid),
                    xmlelement(name nbident, grundstueck.nbident),
                    xmlelement(name art, grundstueck.art),
                    xmlelement(name flaechenmass, grundstueck.flaechenmass),
                    bodbdanteil_xml.bodbdanteil_xmlelement,
                    flurnamen_xml.flurnamen_xmlelement
               )
        ) AS grundstueck
    FROM 
        grundstueck
        LEFT JOIN bodbdanteil_xml
        ON grundstueck.egrid = bodbdanteil_xml.egrid
        LEFT JOIN flurnamen_xml
        ON flurnamen_xml.egrid = grundstueck.egrid
    GROUP BY
        grundstueck.egrid
)
SELECT 
    xmlroot(
        xmlelement(name grundstuecke, xmlagg(pre_result.grundstueck)) 
        , version '1.0', standalone YES
    )
FROM 
    pre_result
;
