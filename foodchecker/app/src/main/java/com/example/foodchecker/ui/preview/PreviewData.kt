package com.example.foodchecker.ui.preview

import com.example.foodchecker.domain.model.Additive
import com.example.foodchecker.domain.model.Allergen
import com.example.foodchecker.domain.model.AllergenMarker
import com.example.foodchecker.domain.model.MedicalRegistry
import com.example.foodchecker.domain.model.ProductCheck
import com.example.foodchecker.domain.model.ProductDetails
import com.example.foodchecker.domain.model.RiskLevel
import com.example.foodchecker.domain.model.ScannedProduct
import com.example.foodchecker.domain.model.UserProfile
import com.example.foodchecker.domain.usecase.AnalyzeIngredientsUseCase
import java.time.LocalDate

internal object PreviewData {

    const val SAUSAGE_BARCODE = "4607012345677"
    const val MAYONNAISE_BARCODE = "4600987123455"
    const val NUT_BAR_BARCODE = "4680019651231"

    private val BaseDate: LocalDate = LocalDate.of(2026, 9, 14)

    val profile = UserProfile(
        name = "Иван Иванов",
        criticalAllergens = setOf(Allergen.PEANUT, Allergen.LACTOSE, Allergen.GLUTEN)
    )

    private val analyzer = AnalyzeIngredientsUseCase()

    private val products: List<ScannedProduct> = listOf(
        product(
            barcode = NUT_BAR_BARCODE,
            name = "Батончик «Ореховый рай»",
            brand = "Sweet Co",
            ingredients = "Арахис жареный (35%), сахар, патока, лесной орех, соевый белок, эмульгатор E322, соль, ароматизатор ванилин."
        ),
        product(
            barcode = "4607025392019",
            name = "Йогурт натуральный 2,5%",
            brand = "MilkHouse",
            ingredients = "Молоко нормализованное, закваска йогуртовых культур."
        ),
        product(
            barcode = "4601234567893",
            name = "Рисовые хлебцы",
            brand = "GrainLife",
            ingredients = "Рис цельнозерновой, соль."
        ),
        product(
            barcode = "4600123409870",
            name = "Овсяное печенье",
            brand = "GrainLife",
            ingredients = null
        ),
        product(
            barcode = "4601987654321",
            name = "Хлеб пшеничный с кунжутом",
            brand = "Bakery Plus",
            ingredients = "Мука пшеничная высшего сорта, вода, дрожжи хлебопекарные, семена кунжута, соль, сахар, консервант E282."
        ),
        product(
            barcode = MAYONNAISE_BARCODE,
            name = "Майонез «Провансаль»",
            brand = "Vkusno",
            ingredients = "Масло подсолнечное рафинированное, вода, яичный желток, сахар, горчичный порошок, соль, уксус, загуститель E1422, консервант E202, регулятор кислотности E330."
        ),
        product(
            barcode = SAUSAGE_BARCODE,
            name = "Колбаса варёная «Докторская»",
            brand = "Мясной двор",
            ingredients = "Свинина, говядина, вода, яйца куриные, молоко сухое, соль, стабилизатор E451, усилитель вкуса E621, фиксатор окраски E250, антиокислитель E300."
        )
    ).mapIndexed { index, item -> item.copy(scannedAt = BaseDate.minusDays(index.toLong())) }

    val checks: List<ProductCheck>
        get() = products.map { analyzer.check(it, profile, PreviewRegistry.registry) }

    fun details(barcode: String): ProductDetails {
        val product = products.first { it.barcode == barcode }
        return ProductDetails(product, analyzer(product.ingredients, profile, PreviewRegistry.registry))
    }

    private fun product(barcode: String, name: String, brand: String, ingredients: String?) =
        ScannedProduct(
            barcode = barcode,
            name = name,
            brand = brand,
            ingredients = ingredients,
            scannedAt = BaseDate
        )
}

private object PreviewRegistry {

    private val additives = listOf(
        additive("E202", "Сорбат калия", "Консервант, подавляет рост плесени и дрожжей. Считается безопасным.", RiskLevel.LOW),
        additive("E250", "Нитрит натрия", "Консервант и фиксатор окраски мяса. При нагревании образует канцерогенные нитрозамины.", RiskLevel.HIGH),
        additive("E282", "Пропионат кальция", "Консервант хлебобулочных изделий. В обычных количествах безопасен.", RiskLevel.LOW),
        additive("E300", "Аскорбиновая кислота", "Антиоксидант, витамин C. Безопасна.", RiskLevel.LOW),
        additive("E322", "Лецитины", "Эмульгатор, получаемый из сои или подсолнечника. Безопасен, но может содержать следы сои.", RiskLevel.LOW),
        additive("E330", "Лимонная кислота", "Регулятор кислотности. В обычных количествах безопасна.", RiskLevel.LOW),
        additive("E451", "Трифосфаты", "Стабилизатор, удерживает воду в мясе. Избыток фосфатов нагружает почки.", RiskLevel.MEDIUM),
        additive("E621", "Глутамат натрия", "Усилитель вкуса. У чувствительных людей может вызывать головную боль.", RiskLevel.MEDIUM),
        additive("E1422", "Ацетилированный дикрахмаладипат", "Модифицированный крахмал, загуститель. Безопасен.", RiskLevel.LOW)
    )

    private val allergenMarkers = listOf(
        marker(Allergen.PEANUT, "Один из самых сильных пищевых аллергенов.", "арахис"),
        marker(Allergen.TREE_NUTS, "Орехи часто вызывают тяжёлые аллергические реакции.", "орех", "фундук", "миндал"),
        marker(Allergen.LACTOSE, "Молочный сахар. При непереносимости вызывает расстройство пищеварения.", "молок", "молоч", "сливк"),
        marker(Allergen.GLUTEN, "Белок злаков. Противопоказан при целиакии.", "пшени", "глютен", "ржан", "овсян"),
        marker(Allergen.EGGS, "Белки яиц — частая причина пищевой аллергии.", "яйц", "яичн", "желток"),
        marker(Allergen.SOY, "Соевый белок может вызывать аллергические реакции.", "соя", "сои", "соев"),
        marker(Allergen.SESAME, "Кунжут опасен даже в следовых количествах.", "кунжут"),
        marker(Allergen.MUSTARD, "Горчица вызывает реакции вплоть до анафилаксии.", "горчиц", "горчичн")
    )

    val registry: MedicalRegistry
        get() = MedicalRegistry(additives = additives, allergenMarkers = allergenMarkers)

    private fun additive(marker: String, name: String, description: String, risk: RiskLevel) =
        Additive(id = marker.lowercase(), marker = marker, name = name, description = description, risk = risk)

    private fun marker(allergen: Allergen, description: String, vararg keywords: String) =
        AllergenMarker(
            id = "allergen_${allergen.name.lowercase()}",
            allergen = allergen,
            keywords = keywords.toList(),
            description = description
        )
}
