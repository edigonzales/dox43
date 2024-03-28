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
        ST_Intersects(ST_SetSRID(ST_MakePoint(2608026, 1228149), 2056), g.geometrie)
)
,
bodenbedeckung AS 
(
    SELECT 
        art_txt, 
        b.geometrie
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
        ST_Area(ST_Union(geom)) AS flaechenmass, 
        art_txt,
        art_txt AS art
    FROM 
        (
            SELECT
                (ST_Dump(ST_CollectionExtract(ST_Intersection(grundstueck.geometrie, bodenbedeckung.geometrie), 3))).geom AS geom, 
                bodenbedeckung.art_txt 
            FROM
                bodenbedeckung,
                grundstueck        
        ) AS foo
    WHERE 
        ST_IsValid(geom) IS TRUE AND geom IS NOT NULL
    GROUP BY
        art_txt 
)
,
bodbdanteil_xml AS 
(
    SELECT
        xmlagg(
            xmlelement(name bodbdanteil, 
                xmlelement(name art, art), 
                xmlelement(name artbezeichnung, art_txt),
                xmlelement(name flaechenmass, flaechenmass)
            )    
        ) AS bodbdanteil_xmlelement
    FROM    
        bodbdanteile
)
,
flurnamen_xml AS 
(
    SELECT 
        xmlagg(
            xmlelement(name flurname, flurname)
        ) AS flurnamen_xmlelement
    FROM 
        agi_mopublic_pub.mopublic_flurname AS f 
        LEFT JOIN grundstueck 
        ON ST_Overlaps(grundstueck.geometrie, f.geometrie)
    WHERE 
        egrid IS NOT NULL
)
SELECT 
    xmlroot(
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
        ,version '1.0', standalone yes
    )
FROM 
    grundstueck,
    bodbdanteil_xml,
    flurnamen_xml
;