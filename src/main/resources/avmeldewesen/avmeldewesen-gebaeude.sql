SELECT 
    egid,
    nachfuehrung,
    ST_Area(geometrie) AS flaeche
FROM 
    agi_mopublic_pub.mopublic_bodenbedeckung_proj
;