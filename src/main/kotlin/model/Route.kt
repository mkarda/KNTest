package model

import org.locationtech.jts.geom.LineString

data class Route(
    var id: String,
    var fromSeq: Int,
    var toSeq: Int,
    var fromPort: String,
    var toPort: String,
    var legDuration: Int,
    var count: Int,
    var geom: LineString
)