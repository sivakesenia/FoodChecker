package com.example.foodchecker.data.sample

import com.example.foodchecker.domain.model.Allergen
import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.model.ScannedProduct
import com.example.foodchecker.domain.model.UserProfile
import java.time.LocalDate

object SampleData {

    val profile = UserProfile(
        name = "Иван Иванов",
        criticalAllergens = setOf(Allergen.PEANUT, Allergen.LACTOSE, Allergen.GLUTEN)
    )

    private val historyProducts = listOf(
        Product(
            barcode = "4680019651234",
            name = "Батончик «Ореховый рай»",
            brand = "Sweet Co",
            ingredients = "Арахис жареный (35%), сахар, патока, лесной орех, соевый белок, эмульгатор E322, соль, ароматизатор ванилин."
        ),
        Product(
            barcode = "4607025392012",
            name = "Йогурт натуральный 2,5%",
            brand = "MilkHouse",
            ingredients = "Молоко нормализованное, закваска йогуртовых культур."
        ),
        Product(
            barcode = "4601234567890",
            name = "Рисовые хлебцы",
            brand = "GrainLife",
            ingredients = "Рис цельнозерновой, соль."
        ),
        Product(
            barcode = "4600123409876",
            name = "Овсяное печенье",
            brand = "GrainLife",
            ingredients = null
        ),
        Product(
            barcode = "4601987654321",
            name = "Хлеб пшеничный с кунжутом",
            brand = "Bakery Plus",
            ingredients = "Мука пшеничная высшего сорта, вода, дрожжи хлебопекарные, семена кунжута, соль, сахар, консервант E282."
        ),
        Product(
            barcode = "4607001234561",
            name = "Тунец в собственном соку",
            brand = "Ocean Line",
            ingredients = "Тунец, вода, соль."
        ),
        Product(
            barcode = "4600987123450",
            name = "Майонез «Провансаль»",
            brand = "Vkusno",
            ingredients = "Масло подсолнечное рафинированное, вода, яичный желток, сахар, горчичный порошок, соль, уксус, загуститель E1422, консервант E202, регулятор кислотности E330."
        ),
        Product(
            barcode = "4602345678901",
            name = "Сок яблочный осветлённый",
            brand = "Sad Fruit",
            ingredients = "Яблочный сок восстановленный, антиокислитель аскорбиновая кислота."
        ),
        Product(
            barcode = "4607012345678",
            name = "Колбаса варёная «Докторская»",
            brand = "Мясной двор",
            ingredients = "Свинина, говядина, вода, яйца куриные, молоко сухое, соль, стабилизатор E451, усилитель вкуса E621, фиксатор окраски E250, антиокислитель E300."
        )
    )

    val catalog: List<Product> = historyProducts + listOf(
        Product(
            barcode = "3017620422003",
            name = "Паста ореховая с какао",
            brand = "Nutella",
            ingredients = "Сахар, пальмовое масло, лесные орехи (13%), обезжиренное сухое молоко (8,7%), обезжиренное какао (7,4%), эмульгатор лецитины (соя), ароматизатор ванилин."
        ),
        Product(
            barcode = "5449000000996",
            name = "Напиток газированный",
            brand = "Coca-Cola",
            ingredients = "Вода, сахар, диоксид углерода, краситель сахарный колер IV (E150d), регулятор кислотности ортофосфорная кислота, натуральные ароматизаторы, кофеин."
        ),
        Product(
            barcode = "4600300031012",
            name = "Чипсы «Сметана и лук»",
            brand = "Crispy",
            ingredients = "Картофель, масло подсолнечное, вкусо-ароматическая добавка (молочная сыворотка, лук, сахар, усилитель вкуса глутамат натрия, E631, E627, краситель E160a), соль."
        ),
        Product(
            barcode = "4601000111226",
            name = "Лимонад «Дюшес»",
            brand = "Vesna",
            ingredients = "Вода питьевая, сахар, регулятор кислотности E330, консервант бензоат натрия, подсластитель аспартам, краситель E102, ароматизатор «Груша»."
        ),
        Product(
            barcode = "4600700812343",
            name = "Мармелад жевательный",
            brand = "Fruity",
            ingredients = "Сахар, глюкозный сироп, желатин, регулятор кислотности лимонная кислота, красители E129, E110, кармин, ароматизаторы."
        )
    )

    val history: List<ScannedProduct> = historyProducts.mapIndexed { index, product ->
        ScannedProduct(
            barcode = product.barcode,
            name = product.name,
            brand = product.brand,
            ingredients = product.ingredients,
            scannedAt = LocalDate.of(2026, 9, 14).minusDays(index.toLong())
        )
    }
}
