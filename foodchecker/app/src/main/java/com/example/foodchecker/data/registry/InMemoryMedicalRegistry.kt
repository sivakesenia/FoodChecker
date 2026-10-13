package com.example.foodchecker.data.registry

import com.example.foodchecker.domain.model.Additive
import com.example.foodchecker.domain.model.Allergen
import com.example.foodchecker.domain.model.AllergenMarker
import com.example.foodchecker.domain.model.RiskLevel
import com.example.foodchecker.domain.repository.MedicalRegistry

class InMemoryMedicalRegistry : MedicalRegistry {

    private val additivesByMarker = Additives.associateBy { it.marker.lowercase() }

    override fun additives(): List<Additive> = Additives

    override fun allergenMarkers(): List<AllergenMarker> = AllergenMarkers

    override fun findAdditive(marker: String): Additive? {
        val key = marker.lowercase()
        return additivesByMarker[key] ?: additivesByMarker[key.trimEnd { it.isLetter() }]
    }

    private companion object {
        val Additives = listOf(
            Additive(
                id = "e100",
                marker = "E100",
                name = "Куркумин",
                description = "Натуральный жёлтый краситель из корня куркумы. Считается безопасным.",
                risk = RiskLevel.LOW,
                synonyms = listOf("куркумин")
            ),
            Additive(
                id = "e102",
                marker = "E102",
                name = "Тартразин",
                description = "Синтетический жёлтый краситель. Может вызывать аллергические реакции и гиперактивность у детей.",
                risk = RiskLevel.HIGH,
                synonyms = listOf("тартразин")
            ),
            Additive(
                id = "e110",
                marker = "E110",
                name = "Жёлтый «солнечный закат»",
                description = "Синтетический краситель. Связан с гиперактивностью и нарушением внимания у детей.",
                risk = RiskLevel.HIGH
            ),
            Additive(
                id = "e120",
                marker = "E120",
                name = "Кармин",
                description = "Натуральный красный краситель из насекомых. Возможны аллергические реакции.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("кармин", "кошениль")
            ),
            Additive(
                id = "e129",
                marker = "E129",
                name = "Красный очаровательный",
                description = "Синтетический азокраситель. Связан с гиперактивностью у детей.",
                risk = RiskLevel.HIGH
            ),
            Additive(
                id = "e150d",
                marker = "E150d",
                name = "Сахарный колер IV",
                description = "Краситель, получаемый аммиачно-сульфитным способом. При производстве образуется 4-метилимидазол.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("сахарный колер")
            ),
            Additive(
                id = "e160a",
                marker = "E160a",
                name = "Каротины",
                description = "Натуральный оранжевый краситель, провитамин A. Безопасен.",
                risk = RiskLevel.LOW,
                synonyms = listOf("каротин")
            ),
            Additive(
                id = "e202",
                marker = "E202",
                name = "Сорбат калия",
                description = "Консервант, подавляет рост плесени и дрожжей. Считается безопасным.",
                risk = RiskLevel.LOW,
                synonyms = listOf("сорбат калия")
            ),
            Additive(
                id = "e211",
                marker = "E211",
                name = "Бензоат натрия",
                description = "Консервант. В сочетании с аскорбиновой кислотой может образовывать бензол.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("бензоат натрия")
            ),
            Additive(
                id = "e220",
                marker = "E220",
                name = "Диоксид серы",
                description = "Консервант. Может провоцировать бронхоспазм у людей с астмой и чувствительностью к сульфитам.",
                risk = RiskLevel.HIGH,
                synonyms = listOf("диоксид серы", "сернистый ангидрид")
            ),
            Additive(
                id = "e250",
                marker = "E250",
                name = "Нитрит натрия",
                description = "Консервант и фиксатор окраски мяса. При нагревании образует канцерогенные нитрозамины.",
                risk = RiskLevel.HIGH,
                synonyms = listOf("нитрит натрия")
            ),
            Additive(
                id = "e270",
                marker = "E270",
                name = "Молочная кислота",
                description = "Регулятор кислотности, естественный продукт брожения. Безопасна.",
                risk = RiskLevel.LOW
            ),
            Additive(
                id = "e282",
                marker = "E282",
                name = "Пропионат кальция",
                description = "Консервант хлебобулочных изделий. В обычных количествах безопасен.",
                risk = RiskLevel.LOW,
                synonyms = listOf("пропионат кальция")
            ),
            Additive(
                id = "e300",
                marker = "E300",
                name = "Аскорбиновая кислота",
                description = "Антиоксидант, витамин C. Безопасна.",
                risk = RiskLevel.LOW,
                synonyms = listOf("аскорбиновая кислота")
            ),
            Additive(
                id = "e322",
                marker = "E322",
                name = "Лецитины",
                description = "Эмульгатор, получаемый из сои или подсолнечника. Безопасен, но может содержать следы сои.",
                risk = RiskLevel.LOW,
                synonyms = listOf("лецитин")
            ),
            Additive(
                id = "e330",
                marker = "E330",
                name = "Лимонная кислота",
                description = "Регулятор кислотности. В обычных количествах безопасна, при избытке раздражает эмаль зубов.",
                risk = RiskLevel.LOW,
                synonyms = listOf("лимонная кислота")
            ),
            Additive(
                id = "e338",
                marker = "E338",
                name = "Ортофосфорная кислота",
                description = "Регулятор кислотности. При регулярном употреблении может нарушать усвоение кальция.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("ортофосфорная кислота")
            ),
            Additive(
                id = "e407",
                marker = "E407",
                name = "Каррагинан",
                description = "Загуститель из красных водорослей. Может раздражать слизистую кишечника.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("каррагинан")
            ),
            Additive(
                id = "e412",
                marker = "E412",
                name = "Гуаровая камедь",
                description = "Натуральный загуститель. Безопасен.",
                risk = RiskLevel.LOW,
                synonyms = listOf("гуаровая камедь")
            ),
            Additive(
                id = "e415",
                marker = "E415",
                name = "Ксантановая камедь",
                description = "Загуститель микробиологического происхождения. Безопасен.",
                risk = RiskLevel.LOW,
                synonyms = listOf("ксантан")
            ),
            Additive(
                id = "e451",
                marker = "E451",
                name = "Трифосфаты",
                description = "Стабилизатор, удерживает воду в мясе. Избыток фосфатов нагружает почки.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("трифосфат")
            ),
            Additive(
                id = "e471",
                marker = "E471",
                name = "Моно- и диглицериды жирных кислот",
                description = "Эмульгатор. В целом безопасен.",
                risk = RiskLevel.LOW,
                synonyms = listOf("моно- и диглицериды")
            ),
            Additive(
                id = "e500",
                marker = "E500",
                name = "Карбонаты натрия",
                description = "Разрыхлитель, пищевая сода. Безопасен.",
                risk = RiskLevel.LOW,
                synonyms = listOf("гидрокарбонат натрия")
            ),
            Additive(
                id = "e621",
                marker = "E621",
                name = "Глутамат натрия",
                description = "Усилитель вкуса. У чувствительных людей может вызывать головную боль и учащённое сердцебиение.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("глутамат натрия")
            ),
            Additive(
                id = "e627",
                marker = "E627",
                name = "Гуанилат натрия",
                description = "Усилитель вкуса. Не рекомендуется при подагре и астме.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("гуанилат натрия")
            ),
            Additive(
                id = "e631",
                marker = "E631",
                name = "Инозинат натрия",
                description = "Усилитель вкуса. Не рекомендуется при подагре.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("инозинат натрия")
            ),
            Additive(
                id = "e950",
                marker = "E950",
                name = "Ацесульфам калия",
                description = "Подсластитель. Долгосрочное влияние на организм изучено недостаточно.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("ацесульфам")
            ),
            Additive(
                id = "e951",
                marker = "E951",
                name = "Аспартам",
                description = "Подсластитель. Противопоказан при фенилкетонурии, ВОЗ относит к возможным канцерогенам.",
                risk = RiskLevel.HIGH,
                synonyms = listOf("аспартам")
            ),
            Additive(
                id = "e955",
                marker = "E955",
                name = "Сукралоза",
                description = "Подсластитель. При сильном нагревании может образовывать вредные хлорсодержащие соединения.",
                risk = RiskLevel.MEDIUM,
                synonyms = listOf("сукралоза")
            ),
            Additive(
                id = "e1422",
                marker = "E1422",
                name = "Ацетилированный дикрахмаладипат",
                description = "Модифицированный крахмал, загуститель. Безопасен.",
                risk = RiskLevel.LOW
            )
        )

        val AllergenMarkers = listOf(
            AllergenMarker(
                id = "allergen_peanut",
                allergen = Allergen.PEANUT,
                keywords = listOf("арахис"),
                description = "Один из самых сильных пищевых аллергенов, может вызвать анафилактический шок."
            ),
            AllergenMarker(
                id = "allergen_tree_nuts",
                allergen = Allergen.TREE_NUTS,
                keywords = listOf("орех", "фундук", "миндал", "кешью", "фисташ", "пекан"),
                description = "Орехи часто вызывают тяжёлые аллергические реакции, в том числе отёк Квинке.",
                exclusions = listOf(
                    // Мускатный орех — пряность, а не орех; кокос обычно не относят к аллергенным орехам.
                    "(мускатн|кокосов)\\p{L}*\\s+орех\\p{L}*",
                    "орех\\p{L}*\\s+(мускатн|кокосов)\\p{L}*"
                )
            ),
            AllergenMarker(
                id = "allergen_lactose",
                allergen = Allergen.LACTOSE,
                keywords = listOf("молок", "молоч", "лактоз", "сливк", "сливоч", "сыворот", "казеин", "кефир", "творог"),
                description = "Молочный сахар. При непереносимости вызывает вздутие и расстройство пищеварения.",
                exclusions = listOf(
                    // Растительное «молоко» и «сливки» не содержат лактозы.
                    "(кокосов|соев|миндальн|овсян|рисов|орехов)\\p{L}*\\s+(молок|сливк|сливоч)\\p{L}*",
                    "(молок|сливк)\\p{L}*\\s+(кокосов|соев|миндальн|овсян|рисов|орехов)\\p{L}*",
                    // Молочная кислота (E270) получается брожением и лактозы не содержит.
                    "молочн\\p{L}*\\s+кислот\\p{L}*"
                )
            ),
            AllergenMarker(
                id = "allergen_gluten",
                allergen = Allergen.GLUTEN,
                keywords = listOf("пшени", "глютен", "клейковин", "ячмен", "рожь", "ржан", "овсян", "солод"),
                description = "Белок злаков. Противопоказан при целиакии и непереносимости глютена.",
                exclusions = listOf(
                    // Солодка (лакрица) — растение, не злак.
                    "солодк\\p{L}*",
                    // Без глютена: «безглютеновый», «без глютена».
                    "безглютен\\p{L}*",
                    "без\\s+глютена\\p{L}*"
                )
            ),
            AllergenMarker(
                id = "allergen_eggs",
                allergen = Allergen.EGGS,
                keywords = listOf("яйц", "яичн", "меланж", "желток"),
                description = "Белки яиц — частая причина пищевой аллергии, особенно у детей."
            ),
            AllergenMarker(
                id = "allergen_soy",
                allergen = Allergen.SOY,
                keywords = listOf("соя", "сои", "соев"),
                description = "Соевый белок может вызывать кожные и респираторные аллергические реакции."
            ),
            AllergenMarker(
                id = "allergen_fish",
                allergen = Allergen.FISH,
                keywords = listOf("рыб", "тунец", "тунц", "лосос", "треск", "сельдь", "сельди", "анчоус", "скумбри"),
                description = "Аллергия на рыбу обычно сохраняется на всю жизнь и может быть тяжёлой."
            ),
            AllergenMarker(
                id = "allergen_shellfish",
                allergen = Allergen.SHELLFISH,
                keywords = listOf("кревет", "краб", "мидии", "мидий", "кальмар", "устриц", "моллюск"),
                description = "Морепродукты — одна из частых причин анафилаксии у взрослых."
            ),
            AllergenMarker(
                id = "allergen_sesame",
                allergen = Allergen.SESAME,
                keywords = listOf("кунжут", "тахин"),
                description = "Кунжут может вызывать тяжёлые реакции даже в следовых количествах."
            ),
            AllergenMarker(
                id = "allergen_mustard",
                allergen = Allergen.MUSTARD,
                keywords = listOf("горчиц", "горчичн"),
                description = "Горчица вызывает аллергические реакции вплоть до анафилаксии."
            ),
            AllergenMarker(
                id = "allergen_celery",
                allergen = Allergen.CELERY,
                keywords = listOf("сельдере"),
                description = "Сельдерей — распространённый аллерген, часто входит в состав приправ."
            ),
            AllergenMarker(
                id = "allergen_sulfites",
                allergen = Allergen.SULFITES,
                keywords = listOf("сульфит", "диоксид серы", "сернист"),
                description = "Сульфиты могут провоцировать приступы у людей с бронхиальной астмой."
            )
        )
    }
}
