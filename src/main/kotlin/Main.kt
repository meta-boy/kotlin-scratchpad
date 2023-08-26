import codes.*
import java.lang.Error
import java.time.LocalDate
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

fun classToType(promoCode: PromoCode): PromoType {
    return when(promoCode) {
        is GeoCityDevicePromo -> PromoType.GEO_CITY_DEVICE
        is GeoCountryDevicePromo -> PromoType.GEO_COUNTRY_DEVICE
        is GeoCityPromo -> PromoType.GEO_CITY
        is GeoCountryPromo -> PromoType.GEO_COUNTRY
        is DevicePromo -> PromoType.DEVICE
        is TtdPromo -> PromoType.TTD
        is UserPromo -> PromoType.USER
        else -> throw Error()
    }
}

fun main(args: Array<String>) {
    val promoCodes: List<PromoCode> = listOf(
        GeoCityDevicePromo("New York", "Apple", 15),
        GeoCityDevicePromo("Los Angeles", "MWeb", 10),

        GeoCountryDevicePromo("USA", "Desktop", 20),
        GeoCountryDevicePromo("Canada", "Android", 12),
        GeoCountryDevicePromo("USA", "Desktop", 30),
        GeoCountryDevicePromo("Canada", "Android", 22),
        GeoCountryDevicePromo("USA", "Desktop", 40),
        GeoCountryDevicePromo("Canada", "Android", 32),

        GeoCityPromo("London", 8),
        GeoCityPromo("Paris", 10),

        GeoCountryPromo("Germany", 15),
        GeoCountryPromo("Spain", 12),

        DevicePromo("Mobile", 18),
        DevicePromo("Desktop", 22),

        TtdPromo(5),
        TtdPromo(8),

        UserPromo(10),
        UserPromo(12)
    ).shuffled()

    val startDate = LocalDate.now().minusMonths(Random.nextLong(0, 6))
    val endDate = LocalDate.now().plusMonths(3)

    val promos = promoCodes.map {
        Promo(
            name = it.javaClass.name,
            startDate = startDate,
            endDate = endDate,
            type = classToType(it),
            code = it
        )
    }

    val userParams = UserParams(
        city = "Vancouver",
        country = "Canada",
        device = "Android"
    )

    val uniqueValues = createBitmapIndex(promos)
    val cache = createBestPromoMapMethodA(promos)

    val timeA = measureNanoTime {
        val promoKey = constructCacheKey(userParams, uniqueValues)

        val promo = cache[promoKey]

        println(promo)
    }
    println(timeA)

    val timeB = measureNanoTime {
        val applicablePromos = promos.filter {
            it.isValid(userParams)
        }

        val finalPromo = applicablePromos.maxBy { it.code.discount }

        println(finalPromo)
    }

    println(timeB)




}