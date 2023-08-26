package codes

interface PromoCode {
    val discount: Int
}


data class GeoCityDevicePromo(
    val city: String,
    val device: String,
    override val discount: Int

): PromoCode

data class GeoCountryDevicePromo(
    val country: String,
    val device: String,
    override val discount: Int

): PromoCode

data class GeoCityPromo(
    val city: String,
    override val discount: Int

): PromoCode

data class GeoCountryPromo(
    val country: String,
    override val discount: Int

): PromoCode

data class DevicePromo(
    val device: String,
    override val discount: Int

): PromoCode

data class TtdPromo(
    override val discount: Int

) : PromoCode

data class UserPromo(
    override val discount: Int

): PromoCode