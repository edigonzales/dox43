SELECT 
    xmlroot(
        xmlelement(name ewsstandort,
            xmlelement(name gbnr, 3317),
            xmlelement(name grundbuch, 'Solothurn'),
            xmlelement(name gemeinde, 'Solothurn'),
            xmlelement(name easting, 2605775),
            xmlelement(name northing, 1228417),
            xmlelement(name tiefe, 100),
            xmlelement(name tiefe_grund, 'Instabiler_UG'),
            xmlelement(name grundwasser, true),
            xmlelement(name getmap, 'https://geo.so.ch/ows/somap?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=false&LAYERS=ch.so.agi.hintergrundkarte_ortho,ch.so.agi.av.grundstuecke&STYLES=&SRS=EPSG%3A2056&CRS=EPSG%3A2056&TILED=false&OPACITIES=255&DPI=96&WIDTH=600&HEIGHT=480&BBOX=2607821.625%2C1228212.5%2C2607980.375%2C1228339.5&MARKER=X-%3E2607901%7CY-%3E1228276')        
        )
        ,version '1.0', standalone YES
    )
;