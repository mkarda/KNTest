import model.Route
import org.locationtech.jts.geom.*
import org.locationtech.jts.linearref.LengthIndexedLine
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier
import repository.CsvRepository
import java.util.function.Consumer
import kotlin.streams.toList

fun main() {
    val geometryFactory = GeometryFactory()
    val csvRepository = CsvRepository(geometryFactory)

    val routes: MutableList<Route> = csvRepository.getRoutesFromFile()

    val allSimplified = routes.stream().map { route ->
        var simplified = DouglasPeuckerSimplifier.simplify(route.geom, 0.001)
        if (route.fromPort == "DEHAM") {
            simplified = simplified.reverse()
        }
        simplified
    }.toList()

    val probesPerRoute: MutableList<List<Point>> = ArrayList()

    allSimplified.forEach(Consumer { simpleRoute: Geometry? ->
        val pointsOnRoute: MutableList<Point> = ArrayList()
        val lengthIndexed = LengthIndexedLine(simpleRoute)
        val endIndex = lengthIndexed.endIndex
        for (i in 0..100) {
            val indexOfProbe = endIndex * (i / 100.0)
            val coord = lengthIndexed.extractPoint(indexOfProbe)
            pointsOnRoute.add(geometryFactory.createPoint(coord))
        }
        probesPerRoute.add(pointsOnRoute)
    })

    val finalCoordinates: MutableList<Coordinate> = ArrayList()
    for (i in 0..100) {
        val averageX = probesPerRoute.stream().mapToDouble { x: List<Point> -> x[i].x }.average()
        val averageY = probesPerRoute.stream().mapToDouble { x: List<Point> -> x[i].y }.average()
        finalCoordinates.add(Coordinate(averageX.asDouble, averageY.asDouble))
    }

    val finaLTrajectory: LineString = geometryFactory.createLineString(finalCoordinates.toTypedArray())
    println(finaLTrajectory.toText())
}



