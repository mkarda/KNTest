package repository

import model.Route
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import kotlin.streams.toList

class CsvRepository(private val geometryFactory: GeometryFactory) {

    fun getRoutesFromFile(): MutableList<Route> {
        val routes: MutableList<Route> = ArrayList()

        BufferedReader(FileReader("src/main/resources/DEBRV_DEHAM_historical_routes.csv")).use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {

                val routeCsvRecord = line!!.replace("\",\"".toRegex(), "\";\"")
                val values = routeCsvRecord.replace("\"".toRegex(), "").split(";").toTypedArray()
                if (values[0] != "id") {
                    val pointsData = values[7].split("],").toTypedArray()
                    val coordinates = Arrays.stream(pointsData)
                        .map { x: String -> x.replace("\\[".toRegex(), "") }
                        .map { x: String ->
                            val split = x.split(",").toTypedArray()
                            Coordinate(split[0].toDouble(),split[1].toDouble())}
                        .toList()

                    val geom = geometryFactory.createLineString(coordinates.toTypedArray())
                    val singleRoute = Route(
                        values[0], values[1].toInt(), values[2].toInt(), values[3], values[4],
                        values[5].toInt(), values[6].toInt(), geom
                    )
                    routes += singleRoute
                }
            }
        }
        return routes
    }


}