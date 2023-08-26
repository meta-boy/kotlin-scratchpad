import codes.*
import java.time.LocalDate

data class Promo(
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: PromoType,
    val code: PromoCode
) {
    fun isValid(userParams: UserParams): Boolean {
        return when (this.type) {
            PromoType.GEO_CITY_DEVICE -> {
                val code = this.code as GeoCityDevicePromo
                code.city == userParams.city && code.device == userParams.device
            }
            PromoType.GEO_COUNTRY_DEVICE -> {
                val code = this.code as GeoCountryDevicePromo
                code.country == userParams.country && code.device == userParams.device
            }
            PromoType.GEO_CITY -> {
                val code = this.code as GeoCityPromo
                code.city == userParams.city
            }
            PromoType.GEO_COUNTRY -> {
                val code = this.code as GeoCountryPromo
                code.country == userParams.country
            }
            PromoType.DEVICE -> {
                val code = this.code as DevicePromo
                code.device == userParams.device
            }
            PromoType.TTD -> false
            PromoType.USER -> false
        }


    }
}


data class UserParams(
    val city: String?,
    val country: String?,
    val device: String?,
)

data class UniqueValues(
    val cities: Set<String>,
    val countries: Set<String>,
    val devices: Set<String>
)
//
fun createBitmapIndex(promos: List<Promo>): Trie {
    val trie = Trie()

    promos.forEach { promo ->
        when (promo.code) {
            is GeoCityDevicePromo -> {
                trie.insert(promo.code.city)
                trie.insert(promo.code.device)
            }
            is GeoCountryDevicePromo -> {
                trie.insert(promo.code.country)
                trie.insert(promo.code.device)
            }
           is GeoCityPromo -> {
               trie.insert(promo.code.city)
            }
            is GeoCountryPromo -> {
                trie.insert(promo.code.country)
            }
            is DevicePromo -> {
                trie.insert(promo.code.device)
            }
            is TtdPromo -> {
            }
            is UserPromo -> {
            }
        }
    }

    return trie
}
//
fun createIndexFromParams(params: UserParams): List<String> {
    val index = mutableListOf<String>()
    params.city?.let { index.add(it) }
    params.country?.let { index.add(it) }
    params.device?.let { index.add(it) }
    return index
}

fun constructCacheKey(userParams: UserParams, trie: Trie): String? {
    val filteredParams = UserParams(
        city = userParams.city?.takeIf { trie.search(it) },
        country = userParams.country?.takeIf { trie.search(it) },
        device = userParams.device?.takeIf { trie.search(it) }
    )

    val index = createIndexFromParams(filteredParams)

    return index.joinToString(":")
}

fun constructKeyFromPromo(promo: Promo, trie: Trie): String? {
    return when (promo.type) {
        PromoType.GEO_CITY_DEVICE -> {
            val promoCode = promo.code as GeoCityDevicePromo
            if (trie.search(promoCode.city) && trie.search(promoCode.device)) {
                "${promoCode.city}:${promoCode.device}"
            } else {
                null
            }
        }
        PromoType.GEO_COUNTRY_DEVICE -> {
            val promoCode = promo.code as GeoCountryDevicePromo
            if (trie.search(promoCode.country) && trie.search(promoCode.device)) {
                "${promoCode.country}:${promoCode.device}"
            } else {
                null
            }
        }
        PromoType.GEO_CITY -> {
            val promoCode = promo.code as GeoCityPromo
            if (trie.search(promoCode.city)) {
                promoCode.city
            } else {
                null
            }
        }
        PromoType.GEO_COUNTRY -> {
            val promoCode = promo.code as GeoCountryPromo
            if (trie.search(promoCode.country)) {
                promoCode.country
            } else {
                null
            }
        }
        else -> null
    }
}


class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var isEndOfWord = false
}

class Trie {
    val root = TrieNode()

    fun insert(word: String) {
        var node = root
        for (char in word) {
            node.children.getOrPut(char) { TrieNode() }
            node = node.children[char]!!
        }
        node.isEndOfWord = true
    }

    fun search(word: String): Boolean {
        var node = root
        for (char in word) {
            node = node.children[char] ?: return false
        }
        return node.isEndOfWord
    }
}

fun createBestPromoMapMethodA(promos: List<Promo>): Map<String, Promo> {
    val cache = mutableMapOf<String, Promo>()
    val trie = createBitmapIndex(promos)
    promos.forEach { promo ->
        val key = constructKeyFromPromo(promo, trie)
        val existingPromo = cache[key]
        if (existingPromo != null && key != null) {
            if (existingPromo.code.discount < promo.code.discount) {
                cache[key] = promo
            }
        } else {
            if (key != null) cache[key] = promo
        }

    }
    return cache
}