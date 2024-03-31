SELECT
    nummer, 
    egrid,
    ST_Area(geometrie) AS flaechenmass 
FROM 
    agi_mopublic_pub.mopublic_grundstueck_proj
WHERE 
    art_txt = 'Liegenschaft'
;  