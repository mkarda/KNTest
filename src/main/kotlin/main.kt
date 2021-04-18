import model.Route
import org.locationtech.jts.geom.*
import org.locationtech.jts.linearref.LengthIndexedLine
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier
import repository.CsvRepository
import java.util.function.Consumer
import kotlin.streams.toList

fun main() {
    val geometryFactory = GeometryFactory()
    val csvRepository = CsvRepository()

    val routes: MutableList<Route> = csvRepository.getRoutesFromFile()

    val allSimplified = routes.stream().map { route ->
        val geom = route.geom
        var simplified = DouglasPeuckerSimplifier.simplify(geom, 0.001)
        if (route.fromPort == "DEHAM") {
            simplified = simplified.reverse()
        }
        simplified
    }.toList()

    val all: MutableList<List<Point>> = ArrayList()

    allSimplified.forEach(Consumer { simpleRoute: Geometry? ->
        val pointsOnRoute: MutableList<Point> = ArrayList()
        val lengthIndexed = LengthIndexedLine(simpleRoute)
        val endIndex1 = lengthIndexed.endIndex
        for (i in 0..100) {
            val v = endIndex1 * (i / 100.0)
            val coord = lengthIndexed.extractPoint(v)
            pointsOnRoute.add(geometryFactory.createPoint(coord))
        }
        all.add(pointsOnRoute)
    })

    val coordinateList: MutableList<Coordinate> = ArrayList()
    for (i in 0..100) {
        val averageX = all.stream().mapToDouble { x: List<Point> -> x[i].x }.average()
        val averageY = all.stream().mapToDouble { x: List<Point> -> x[i].y }.average()
        coordinateList.add(Coordinate(averageX.asDouble, averageY.asDouble))
    }

    val lineString: LineString = geometryFactory.createLineString(coordinateList.toTypedArray())
    println(lineString.toText())
}



