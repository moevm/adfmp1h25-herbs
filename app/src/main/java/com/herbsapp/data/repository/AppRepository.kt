package com.herbsapp.data.repository

import android.content.Context
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.herbsapp.R
import com.herbsapp.data.room.dao.RoomDao
import com.herbsapp.data.room.entity.ElementEntity
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext

class AppRepository(val dao: RoomDao, val applicationContext: Context, val auth: FirebaseAuth) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun signup(name: String, email: String, password: String): Resource<FirebaseUser> {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            return Resource.Success(result.user!!)
        } catch (e: Exception) {
            return Resource.Failure(e)
        }
    }

    suspend fun changeName(name: String): Resource<String> {
        return try {
            if (auth.currentUser != null) {
                auth.currentUser!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build()).await()
                Resource.Success("success")
            } else {
                Resource.Failure(Exception("user is no login"))
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun changeMail(mail: String): Resource<String> {
        return try {
            var user = auth.currentUser
            if (user != null) {
                user!!.reload().await()
                user = auth.currentUser

                if (user != null) {
                    auth.currentUser!!.verifyBeforeUpdateEmail(mail).await()
                } else {
                    println("----------no-login")
                    Resource.Failure(Exception("user is no login"))
                }
                Resource.Success("success")
            } else {
                println("----------no-login")
                Resource.Failure(Exception("user is no login"))
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getHerbs(): Flow<List<HerbEntity>> = flow {
        if (dao.getHerbs().isNullOrEmpty()) {
            dao.upsertHerbs(listOf(
                HerbEntity(
                    id = 0,
                    name = applicationContext.getString(R.string.herb_name_1),
                    title = applicationContext.getString(R.string.herb_title_1),
                    description = applicationContext.getString(R.string.herb_description_1),
                    city = "СПБ",
                    views = 30,
                    rating = 4.5f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/5/58/Cirsium_perplexans.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/Farm_weeds_of_Canada_%281906%29_%2814796102803%29.jpg/1024px-Farm_weeds_of_Canada_%281906%29_%2814796102803%29.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1f/Cirsium_arvense_with_Bees_Richard_Bartz.jpg/1024px-Cirsium_arvense_with_Bees_Richard_Bartz.jpg",
                    ),
                    mClass = applicationContext.getString(R.string.class_4),
                    genus = applicationContext.getString(R.string.genus_4),
                    taste = applicationContext.getString(R.string.taste_4),
                    family = applicationContext.getString(R.string.family_4),
                    isLiked = false
                ),
                HerbEntity(
                    id = 1,
                    name = applicationContext.getString(R.string.herb_name_2),
                    title = applicationContext.getString(R.string.herb_title_2),
                    description = applicationContext.getString(R.string.herb_description_2),
                    city = "СПБ",
                    views = 47,
                    rating = 4.6f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/Aegopodium_podagraria%2C_2022-05-29%2C_West_End_Park%2C_02.jpg/275px-Aegopodium_podagraria%2C_2022-05-29%2C_West_End_Park%2C_02.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Illustration_Aegopodium_podagraria0_clean.jpg/800px-Illustration_Aegopodium_podagraria0_clean.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/8/83/Aegopodium_podagraria_-_stem_profile.jpg",
                    ),
                    mClass = applicationContext.getString(R.string.class_1),
                    genus = applicationContext.getString(R.string.genus_1),
                    taste = applicationContext.getString(R.string.taste_1),
                    family = applicationContext.getString(R.string.family_1),
                    isLiked = false
                ),
                HerbEntity(
                    id = 2,
                    name = applicationContext.getString(R.string.herb_name_3),
                    title = applicationContext.getString(R.string.herb_title_3),
                    description = applicationContext.getString(R.string.herb_description_3),
                    city = "СПБ",
                    views = 98,
                    rating = 5.0f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/Anethum_graveolens_02.jpg/275px-Anethum_graveolens_02.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Illustration_Anethum_graveolens_clean.jpg/800px-Illustration_Anethum_graveolens_clean.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/c/c9/Anethum_graveolens20090812_475.jpg",
                    ),
                    mClass = applicationContext.getString(R.string.class_2),
                    genus = applicationContext.getString(R.string.genus_2),
                    taste = applicationContext.getString(R.string.taste_2),
                    family = applicationContext.getString(R.string.family_2),
                    isLiked = false
                ),
                HerbEntity(
                    id = 3,
                    name = applicationContext.getString(R.string.herb_name_4),
                    title =  applicationContext.getString(R.string.herb_title_4),
                    description = applicationContext.getString(R.string.herb_description_4),
                    city = "СПБ",
                    views = 25,
                    rating = 4.8f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/Chenopodium_album01.jpg/275px-Chenopodium_album01.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/f/fb/Chenopodium_album_Sturm27.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Chenopodium_album_flowers.jpg/1920px-Chenopodium_album_flowers.jpg"
                    ),
                    mClass = applicationContext.getString(R.string.class_3),
                    genus = applicationContext.getString(R.string.genus_3),
                    taste = applicationContext.getString(R.string.taste_3),
                    family = applicationContext.getString(R.string.family_3),
                    isLiked = false
                ),
                HerbEntity(
                    id = 4,
                    name = "Ромашка обыкновенная",
                    title = "Ромашка обыкновенная в Санкт-Петербурге: использование и знакомство",
                    description = "Рома́шка (лат. Matricária) \n— род однолетних цветковых растений семейства астровые, или сложноцветные (Asteraceae), по современной классификации объединяет около 70 видов[3] невысоких пахучих трав[4], цветущих с первого года жизни. Наиболее известный вид — Ромашка аптечная (Matricaria chamomilla, syn. Matricaria recutita), это растение широко используется в лечебных и косметических целях.\n" +
                            "\n" +
                            "Часто ромашкой (с ботанической точки зрения ошибочно) называют виды растений других родов семейства Астровые, таких как Астра, Гербера, Дороникум, Нивяник, Остеоспермум, Пижма, Пупавка, Трёхрёберник, Хризантема, для соцветий-корзинок которых характерны краевые язычковые цветки с белыми или другой окраски лепестками и более тёмная центральная часть соцветия. Эти соцветия напоминают единый цветок, поэтому называются антодий.\nНаучное (латинское) название рода, Matricaria («маточная трава»), происходит от латинского matrix («матка»), что объясняется традиционным применением растения при лечении гинекологических заболеваний. Впервые это название использовал швейцарский ботаник и врач Альбрехт фон Галлер (1708—1777).\n" +
                            "\n" +
                            "Римский писатель и учёный Плиний Старший в своём многотомном труде «Естественная история» описал это растение под названием Chamaemellon, название которого происходит от греч. χᾰμαι (chamai, «низко») и μῆλον (mellon, «яблоко»), что объясняется небольшой высотой травы и запахом цветков, напоминающим запах яблок.\n" +
                            "\n" +
                            "На Руси эти растения именовались «роман», «романова трава», «романов цвет», «романник». Общепризнано, что эти названия восходят именно к прилагательному romanum (romana) в указанных латинских названиях, при этом одни авторы допускают возможность прямого заимствования слова из латыни западноевропейских средневековых травников и лечебников, другие же считают, что имело место заимствование из польского языка, выступившего в качестве языка-посредника[5]. Что касается слова «ромашка», то оно является уменьшительной формой от «роман» и впервые в литературе это слово было зафиксировано в 1778 году у А. Т. Болотова в его книге «Сельский житель»[5].",
                    city = "СПБ",
                    views = 66,
                    rating = 4.2f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Chamomile_Flowers_-_Flickr_-_Swallowtail_Garden_Seeds.jpg/1920px-Chamomile_Flowers_-_Flickr_-_Swallowtail_Garden_Seeds.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/Matricaria_recutita_koe.jpg/1024px-Matricaria_recutita_koe.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9f/20180511Matricaria_discoidea1.jpg/1920px-20180511Matricaria_discoidea1.jpg",
                    ),
                    mClass = "Asteraceae",
                    family = "Ромашковые",
                    taste = applicationContext.getString(R.string.taste_1),
                    genus = "Matricaria",
                    isLiked = false
                ),
                HerbEntity(
                    id = 5,
                    name = "Мелисса лекарственная",
                    title = "Мелисса́ лека́рственная (лат. Melissa officinalis)\nМелисса лекарственная: полезные свойства и советы по уходу",
                    description = "Мелисса является популярным травянистым растением, известным своим ароматом. В Санкт-Петербурге ее можно встретить как в садах, так и на балконах. Это растение обладает множеством целебных свойств, включая успокаивающее действие. Чай из мелиссы помогает при тревоге и бессоннице, а также отлично дополнит десерты." +
                            "\nМноголетнее растение высотой 30—120 см.\n" +
                            "\n" +
                            "Корневище сильно ветвится.\n" +
                            "\n" +
                            "Стебель разветвлённый, четырёхгранный, опушённый короткими волосками с примесью желёзок или почти голый.\n" +
                            "\n" +
                            "Листья супротивные, черешковые, яйцевидные до закруглённо-ромбических, городчато-пильчатые, опушённые.\n" +
                            "\n" +
                            "Цветки собраны в ложные кольца по 6—12; чашечка с нижними шиловидными зубцами, длинноволосистая и желёзистая; венчик синевато-белый или бледно-лиловый. Четыре тычинки, пестик с четырёхраздельной верхней завязью и длинным столбиком.\n" +
                            "\n" +
                            "Плод — крупный, состоит из четырёх орешков яйцевидной формы, чёрного цвета, блестящий. Масса 1000 семян — в среднем 0,62 г. Семена сохраняют всхожесть 2-3 года.\n" +
                            "\n" +
                            "Цветёт в июне—августе. Плоды созревают в августе—сентябре.\n" +
                            "\n" +
                            "Мелисса менее зимостойка, чем котовник кошачий. Поскольку мелиссу и котовник часто путают, важно обратить внимание на внешние отличия. Котовник образует на концах ветвей верхушечные соцветия, а у мелиссы они отсутствуют. Цветки у неё располагаются мутовками в пазухах листьев верхней части стебля. Листья мелиссы светлые, ярко-зелёные, а у котовника они имеют матово-сероватый оттенок." ,
                    city = "СПБ",
                    views = 19,
                    rating = 4.4f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/9/90/Melissa_officinalis.jpg",
                        "https://cdn.stroylandiya.ru/upload/iblock/81f/c8oy5at26zd3uo3oo1vckcjfmvcs6dci.jpg"
                    ),
                    mClass = "Lamiaceae",
                    family = "Яснотковые",
                    taste = "Освежающий",
                    genus = "Melissa",
                    isLiked = false
                ),
                HerbEntity(
                    id = 6,
                    name = "Шалфей лекарственный",
                    title = "Шалфе́й, или Са́львия (лат. Salvia): применение в кулинарии и медицине",
                    description = "Шалфей лекарственный – это многолетнее травянистое растение, популярное как в кулинарии, так и в медицине. В Санкт-Петербурге шалфей часто выращивают в садах и на приусадебных участках. У него характерный аромат и множество полезных свойств, включая антисептическое действие. Настой шалфея помогает при заболеваниях горла и улучшает пищеварение."
                    + "\nМноголетнее растение высотой 20—70 см.\n" +
                            "\n" +
                            "Корень деревянистый, мощный, разветвлённый, внизу густомочковатый.\n" +
                            "\n" +
                            "Стебель прямой, ветвистый, сильно-облиственный, снизу деревянистый, сверху травянистый, четырёхгранный, зимой в верхней части отмирающий, беловато-шерстистый от длинных волнистых волосков.\n" +
                            "\n" +
                            "Листья вегетативных побегов — стеблевые продолговатые супротивные, длиной 3,5—8 см, шириной 0,8—1,5 (до 4) см, туповатые или острые, при основании клиновидные или закруглённые, по краю мелко городчатые, морщинистые, нижние и средние на длинных черешках, верхние — сидячие. Прицветные — ланцетные, сидячие, в несколько раз меньше стеблевых. Жилкование сетчатое. Листья густоопушённые, серо-зелёные.\n" +
                            "\n" +
                            "Соцветия представлены колосовидным тирсом простые или ветвистые, с шестью-семью расставленными 10-цветковыми ложными мутовками; чашечка длиной 9—10 мм, почти до половины надрезанная на две губы; венчик фиолетовый, в два раза длиннее чашечки; столбик немного выставляется из венчика; рыльце с двумя неравными лопастями.\n" +
                            "\n" +
                            "Плод — орешек, диаметром 2,5 мм, почти округлый, тёмно-бурый, сухой, из четырёх долей.\n" +
                            "\n" +
                            "Биологические особенности\n" +
                            "В первый год жизни шалфей лекарственный образует к осени мощный куст.\n" +
                            "\n" +
                            "Растение перекрёстноопыляемое.\n" +
                            "\n" +
                            "Цветёт в июне — июле. Плоды созревают в августе — сентябре. Начинает цвести со второго года. Семена сохраняют всхожесть три года.",
                    city = "СПБ",
                    views = 20,
                    rating = 3.5f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/6/62/Salvia_officinalis_jfg1.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_38%29_%286972241336%29.jpg/1024px-Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_38%29_%286972241336%29.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Salvia_officinalis.jpg/1920px-Salvia_officinalis.jpg",
                    ),
                    mClass = "Lamiaceae",
                    family = "Яснотковые",
                    taste = "Горький",
                    genus = "Salvia",
                    isLiked = false
                ),
                HerbEntity(
                    id = 7,
                    name = "Лаванда",
                    title = "Лава́нда (лат. Lavandula) \n— род растений семейства яснотковых (Lamiaceae или Labiatae). Включает 47[1] видов. Произрастает на Канарских островах, в северной и восточной Африке, в Австралии, на юге Европы, в Аравии и в Индии. Культурные формы выращиваются в садах во всём мире.\n",
                    description = "Лаванда – это не только красивое декоративное растение, но и ценное эфирномасличное. В Санкт-Петербурге её можно встретить в частных садах. Долговечная и неприхотливая, лаванда привлекает пчел и других опылителей. Эфирное масло лаванды широко используется в ароматерапии, помогает справиться со стрессом и улучшает качество сна." +
                            "\nРод представлен травами, полукустарниками или кустарниками диаметром от 45 до 120 см, высотой от 45 до 120 см. Растения характеризуются насыщенным цветением голубого, фиолетового, розового или белого цвета: все зависит от сорта и вида лаванды. Культурных видов лаванды три, а сортов в мире более тридцати. Может выращиваться на черноземах, песчаных, малопродуктивных и каменистых почвах.\n" +
                            "\n" +
                            "Листья супротивные, линейные или линейно-ланцетные, с завернутыми краями, опушенные. Цветки — двуполые, голубовато-фиолетовые или синие (гибридные — других цветов), собранные на концах побегов в колосовидные соцветия.\n" +
                            "\n" +
                            "Плод — эллипсовидный темно-бурый орешек[2]." +
                            "\nЛаванда применяется в парфюмерной промышленности и медицине. Из цветков лаванды получают ценное лавандовое масло. Соцветия лаванды используют для заваривания чаев. Существует достаточно много рецептов чаев с применением лаванды. Также в Венгрии изготавливают мороженое из лаванды, собранной с полей в северной части озера Балатон. В последнее время в кафе можно встретить лавандовый кофе.",
                    city = "СПБ",
                    views = 109,
                    rating = 4.8f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Lavandula_Macro.JPG/1920px-Lavandula_Macro.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/2/2d/Lavandula_canariensis_1.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/7/7e/Lavandula_pedunculata.JPG",
                    ),
                    mClass = "Lamiaceae",
                    family = "Яснотковые",
                    taste = "Цветочный",
                    genus = "Lavandula",
                    isLiked = false
                ),
                HerbEntity(
                    id = 8,
                    name = "Череда",
                    title = "Череда́ (лат. Bídens) \n— род травянистых растений семейства Астровые (Asteraceae), входит в трибу Кореопсисовые (Coreopsideae).\n" +
                            "\n" +
                            "Череда - полезное многолетнее растение для здоровья",
                    description = """
            Череда - это травянистое растение, известное своими целебными свойствами. В медицине она используется для лечения различных кожных заболеваний.
            Листья и цветы череды собирают во время цветения и используют в настойках и отварах.
            Кроме того, ее можно применять как ингредиент в пищу, особенно в виде чая.
            Череда также обладает противовоспалительными свойствами и может помочь при аллергиях.
            
            Русское название происходит от слова "чередовать", названо растение так из-за своего способа роста и распространения. Оно обладает способностью чередовать свои листья вдоль стебля, что создает характерный узор. Также, трава череда образует густые ковры на земле, поэтому ее ещё называют ковровой травой.
            Латинское название bidens образовано от лат. bi- — «двух-» и dens — «зуб», «зубец», что связано с наличием на плодах растения двух (у некоторых видов — четырёх) остевидных отростков[4][5].
        """.trimIndent(),
                    city = "СПБ",
                    views = 56,
                    rating = 3.8f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Bidens_tripartita1_eF.jpg/1024px-Bidens_tripartita1_eF.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/1/1d/Bidens_tripartita.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/6/61/Bidens_tripartita_MHNT.BOT.2004.0.774.jpg/1920px-Bidens_tripartita_MHNT.BOT.2004.0.774.jpg"
                    ),
                    mClass = "Астровые",
                    family = "Астровые",
                    taste = "Нейтральный",
                    genus = "Chrysanthemum",
                    isLiked = false
                ),
                HerbEntity(
                    id = 9,
                    name = "Ranunculus aquatilis\n",
                    title = "Ranunculus aquatilis (лат.) — травянистое растение, вид рода Лютик (Ranunculus) семейства Лютиковые (Ranunculaceae).",
                    description = """
            Ranunculus aquatilis — многолетнее водное растение, образующее коврообразные покрытия на поверхности воды. Имеет ветвистые нитевидные подводные листья и зубчатые плавающие листья. Листья — 2-3 см. В речных потоках с быстрым течением плавающие листья-поплавки могут и не образовываться. Цветки имеют белые лепестки с жёлтыми центрами и держатся на 1-2 см над водой. Плавающие листья являются опорами для цветков[2]. Плод-семянка 1,5-2 мм длиной с тонкими краями, сжатый с булавкообразным крючком длиной 0,6 мм[3]. Вид гексаплоидный с числом хромосом 2n = 48[4].
            
            Вид встречается в большей части Европы, западной части Северной Америки, а также в северо-западной Африке[1][6]. Встречается в стоячих водоёмах (озёрах и прудах) или медленно текущих реках и ручьях, в основном богатых питательными веществами, мезо- и эвтрофных, мутных, но не сильно загрязнённых водах, особенно в канавах и прудах[3][7].
        """.trimIndent(),
                    city = "СПБ",
                    views = 59,
                    rating = 4.8f,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Ranunculus_aquatilis_%28habitat%29.jpg/1920px-Ranunculus_aquatilis_%28habitat%29.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Ranunculus_aquatilis_plant.jpg/1920px-Ranunculus_aquatilis_plant.jpg"
                    ),
                    mClass = "Ranunculus aquatilis",
                    family = "Лютиковые",
                    taste = "Горький",
                    genus = "Лютик",
                    isLiked = false
                ),
                HerbEntity(
                    id = 10,
                    name = "Тысячелистник",
                    title = "Тысячели́стник обыкнове́нный, или Поре́зная трава (лат. Achilléa millefólium) \n— многолетнее травянистое растение; вид рода Тысячелистник (Achillea) семейства Астровые (Asteraceae)\n" +
                            "\n" +
                            "Используется как лекарственное, пряное, декоративное и медоносное растение.",
                    description = "Тысячелистник обыкновенный - многолетнее травянистое растение, известное своими лечебными свойствами. Он обладает противовоспалительным и ранозаживляющим действием. Используется в народной медицине для лечения ран, повреждений кожи, а также в качестве настоя при простудах и гриппе. Именно этот вид помогает поддерживать здоровье благодаря своему богатому химическому составу, включая эфирные масла и флавоноиды.",
                    city = "СПБ",
                    views = 20,
                    rating = 4.5F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Achillea_millefolium_s1.jpg/800px-Achillea_millefolium_s1.jpg",
                    ),
                    mClass = "Asteroideae",
                    family = "Астровые",
                    taste = "Цветочный",
                    genus = "Achillea",
                    isLiked = false
                ),
                HerbEntity(
                    id = 11,
                    name = "Галантус",
                    title = "Галантус (от др.-греч. γάλα, (gála, «молоко») + ἄνθος (ánthos, «цветок»)), или подснежник, \n— небольшой род, насчитывающий около 20 видовлуковичных многолетних травянистых растений из семейства амариллисовых. У растения два линейных листа и один маленький белый поникающий цветок в форме колокольчика с шестью лепестковидными (лепестковидными) чашелистиками в два круга (мутовки). Внутренние лепестки меньшего размера имеют зелёные отметины.",
                    description = "Подснежники были известны с древнейших времён под разными названиями, но в 1753 году получили название Galanthus. По мере увеличения количества признанных видов предпринимались различные попытки разделить их на подгруппы, обычно на основе характера появления листьев (вегетации). В эпоху молекулярной филогенетики было показано, что эта характеристика ненадёжна, и теперь выделяют семь молекулярно определённых кладов, которые соответствуют биогеографическому распределению видов. Продолжают открываться новые виды.\n" +
                            "\n" +
                            "Большинство видов цветут зимой, до весеннего равноденствия (20 или 21 марта в Северном полушарии), но некоторые цветут ранней весной и поздней осенью. Иногда подснежники путают с двумя родственными родами из трибы Galantheae, подснежниками Leucojum и Acis.",
                    city = "СПБ",
                    views = 67,
                    rating = 4.9F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/2/23/Galanthus_nivalis.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Illustration_Galanthus_nivalis0.jpg/800px-Illustration_Galanthus_nivalis0.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/9/95/Leucojum_bulbosum_praecox_Gerard.jpg",
                    ),
                    mClass = "Спаржа",
                    family = "Амариллисовые",
                    taste = "Горьковатый",
                    genus = "Галантус",
                    isLiked = false
                ),
                HerbEntity(
                    id = 12,
                    name = "Фиалка",
                    title = "Фиа́лка (лат. Víola) \n— род растений семейства Фиалковые (Violaceae). Известно около пятисот[2] (по некоторым данным — более пятисот) видов, растущих преимущественно в Северном полушарии — в горах и в регионах с умеренным климатом." +
                            "\nПредставители рода Фиалка встречаются в большинстве регионов мира с умеренным климатом; наибольшая концентрация видов наблюдается в Северной Америке, Андах и Японии.\n" +
                            "\n" +
                            "Многие виды являются характерными эндемичными растениями для южно-американских Анд; небольшое число видов встречается в субтропической Бразилии, в тропической и Южной Африке (в Капской области); в Австралии, Новой Зеландии, на Сандвичевых островах." ,
                    description = "Фиалки — в большей части однолетние или многолетние травянистые растения, изредка полукустарники (виды, растущие в Андах), с попеременными, простыми или перисто-рассечёнными листьями, снабжёнными прилистниками.\n" +
                            "\nЦветки одиночные, пазушные, обоеполые, зигоморфные (открытые и закрытые), околоцветник двойной: пять свободных остающихся чашелистиков с назад обращёнными придатками, пять свободных лепестков, из которых передний со шпорцем. Тычинок пять, они прижаты к пестику, нити у них короткие, передние две тычинки с мешковидным нектарником; связник расширяется над пыльниками в чешуйку. Пестик с верхней, одногнездой, многосемянной завязью, коротким столбиком и головчатым или пластинчатым рыльцем.\n" +
                            "\n" +
                            "Плод — коробочка, вскрывающаяся створками. Семена белковые, с центральным зародышем." +
                            "\nНекоторые виды фиалок — излюбленные растения цветников. Распространены многочисленные сорта растения под названием Анютины глазки. Одни из них разводятся ради пахучих цветков, такова Viola odorata (с бесчисленными садовыми разновидностями, помесями и т. п., есть разновидности с простыми и махровыми цветками, есть и ремонтантные); другие — ради ярких цветков всевозможных цветов (одноцветных, пёстрых, светлых и тёмных), формы и размера, каковы Viola tricolor, Viola altaica, Viola lutea и помеси этих и других видов.",
                    city = "СПБ",
                    views = 55,
                    rating = 4.6F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/3/3c/Viola_tricolor_001.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Пыльник_фиалки.jpg/1920px-Пыльник_фиалки.jpg"
                    ),
                    mClass = "Viola odorata",
                    family = "Фиалковые",
                    taste = "Цветочный",
                    genus = "Фиалка",
                    isLiked = false
                ),
                HerbEntity(
                    id = 13,
                    name = "Печёночница",
                    title = "Печёночница[1], или перелеска[источник не указан 255 дней] (лат. Hepática) \n— род травянистых вечнозелёных лесных растений семейства Лютиковые, распространённых в лесах умеренного пояса Северного полушария.\n" +
                            "\n" +
                            "В средней полосе России в лесах встречается два вида: печеночница благородная (Hepatica nobilis) и печёночница азиатская (Hepatica asiatica), которая растет в горных лесах на юге Приморья.",
                    description = "Многолетние травянистые растения.\n" +
                            "\n" +
                            "Корневище короткое, не утолщённое, клубневидно-волокнистое.\n" +
                            "\n" +
                            "Листья формой напоминают печень, с чем связано название растения, собраны в прикорневую розетку, на более-менее длинных черешках, простые, мало-расчленённые, большей частью трёхлопастные, с цельнокрайными или крупно-зубчатыми лопастями.\n" +
                            "\n" +
                            "Стебли неразветвлённые, в виде стрелки, выходящие из пазух прикорневых листьев или обычно чешуевидных низовых листьев.\n" +
                            "\n" +
                            "Цветочные почки закладываются с осени, цветки распускаются сразу после схода снега. Листочки покрывала большей частью в числе трёх, приближенные к цветку и сильно редуцированные, имеющие обычно вид чашелистиков. Цветки всегда одиночные, небольшие или средних размеров, с 6—10 (редко больше) большей частью довольно узкими листочками околоцветника, могут быть белыми, синими, фиолетовыми, сиреневыми. Тычинки многочисленные, спирально расположенные; пестики волосистые, с коротким прямым столбиком. Цветёт в апреле — мае. После цветения цветоножки удлиняются и цветки пригибаются к земле. Опыление происходит с помощью пчёл, жуков и мух, поедающих пыльцу, так как нектара в цветках нет.\n" +
                            "\n" +
                            "Плоды разносят муравьи, поедающие прозрачный, сочный, белый придаток, заключающий в себе капельку масла[2]. Семенная продукция от 20 до 64 семян на побег.",
                    city = "СПБ",
                    views = 88,
                    rating = 3.3F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Anemone_hepatica_var._japonica_color_variation.JPG/1280px-Anemone_hepatica_var._japonica_color_variation.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Illustration_Hepatica_nobilis0_clean.jpg/800px-Illustration_Hepatica_nobilis0_clean.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Hepatica_nobilis_varianter.JPG/1920px-Hepatica_nobilis_varianter.JPG"
                    ),
                    mClass = "Лютикоцветные",
                    family = "Лютиковые",
                    taste = "Горьковатый",
                    genus = "Печёночница",
                    isLiked = false
                ),
                HerbEntity(
                    id = 14,
                    name = "Ветреница",
                    title = "Ве́треница, Ветряница[2], или Анемо́на (лат. Anemóne) \n— род многолетних травянистых цветковых растений семейства Лютиковые (Ranunculaceae), включает 80 видов[3].\n" +
                            "\n" +
                            "Ареал рода охватывает внетропическую зону Северного полушария, включая Арктику." +
                            "\nСовременное научное название образовано от др.-греч. ἄνεμος — «ветер». Возможно, дословный перевод названия может означать «дочь ветров». Вероятно, название дано растению из-за его чувствительности к ветру, уже при малых порывах которого крупные лепестки цветов начинают трепетать, а цветки раскачиваться на длинных цветоносах. Ранее ошибочно считалось, что цветки растения под действием ветра могут закрываться или распускаться.",
                    description = "Корневище мясистое, цилиндрическое или клубневидное.\n" +
                            "\n" +
                            "Стебли и цветоносы верхушечные, реже в пазухах низовых или прикорневых листьев.\n" +
                            "\n" +
                            "Прикорневые листья иногда отсутствуют, чаще имеются на черешках различной длины, большей частью пальчато-рассечённые или раздельные.\n" +
                            "\n" +
                            "Цветки одиночные или в полузонтичных, часто многоцветковых соцветиях, небольшие или крупные. Околоцветники различной формы с 5—20 листочками и могут быть белого, пурпурного, синего, зелёного, жёлтого, розового или красного цветов. Цветки двуполые и радиально симметричные. Тычинки многочисленные, пестики многочисленные с одной висячей семяпочкой и одним покровом, опушённые или голые, с большей частью коротким, прямым или изогнутым столбиком, иногда без него. Листовые покрывала большей частью в количестве трёх приближены к цветоносам и сильно редуцированы, имеют большей частью вид чашелистиков.\n" +
                            "\n" +
                            "Плоды орешковидные, разнообразной формы, голые или различным образом опушённые, нередко с различными приспособлениями, способствующими распространению их ветром, реже животными.\n" +
                            "\n" +
                            "Растения в культуре размножают корневищами, стеблевыми черенками и клубнями." +
                            "\nРаспространение и среда обитания\n" +
                            "Встречаются преимущественно во внетропической части Северного полушария. Девять видов ветрениц проникают в Арктику (север России, Норвегии, арктическая Аляска, арктическое побережье Канады, острова Банкс и Виктория, побережье Гудзонова залива, Лабрадор)[4].\n" +
                            "\n" +
                            "На территории России и сопредельных стран произрастает около 50 видов ветрениц[5].\n" +
                            "\n" +
                            "Растут по лесам (преимущественно лиственным), кустарникам, опушкам, паркам, тенистым лужайкам; по сырым горным долинам; по сухим холмам, в нижней части лесной зоны гор; по степным лугам, залежам, каменистым обрывам; на субальпийских лугах, травянистых склонах, в горных тундрах; в тундре.",
                    city = "СПБ",
                    views = 12,
                    rating = 5.0F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/Zoo19feb_%287%29.JPG/1920px-Zoo19feb_%287%29.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/3/3c/Anemone_spp_Sturm43.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Anémone_du_Japon_2_FR_2012.jpg/1920px-Anémone_du_Japon_2_FR_2012.jpg"
                    ),
                    mClass = "Лютикоцветные",
                    family = "Лютиковые",
                    taste = "Сладковатый",
                    genus = "Ветреница",
                    isLiked = false
                ),
                HerbEntity(
                    id = 15,
                    name = "Прострел раскрытый",
                    title = "Простре́л раскры́тый, или Со́н-трава́[1] (Pulsatílla pátens) \n— многолетнее травянистое растение, вид рода Прострел (Pulsatilla) семейства Лютиковые (Ranunculaceae)." +
                            "\nВесьма декоративное растение как при цветении, так и в плодах. Культивируется в цветниках (наряду с другими видами прострела).\n" +
                            "\n" +
                            "Препараты растения употребляют как успокаивающее и снотворное средство[4].\n" +
                            "\n" +
                            "В народной медицине сон-трава употребляется от многих болезней. Водный экстракт травы прострела оказывает сильное бактерицидное и фунгицидное действие и используется наружно для быстрейшего заживления ран и при грибковых заболеваниях кожи. Отвар травы пьют в малых дозах при кашле и женских заболеваниях. Настойка водки на траве применяется в качестве растирания при ревматизме. Свежую траву варят в русской печке без воды и выделившимся соком лечат ожоги[4].",
                    description = "Растение 7—15 см высотой.\n" +
                            "\n" +
                            "Корневище мощное, вертикальное, тёмно-коричневое, многоглавое.\n" +
                            "\n" +
                            "Корневые листья на длинных, негусто волосистых черешках, в очертании округло-сердцевидные, дланевидно-трёхрассечённые с ромбическими глубоко-двух-трёхраздельными сегментами и с клиновидными, двух-четырёхнадрезанными или зубчатыми дольками с острыми, часто несколько изогнутыми лопастинками; в молодости, особенно внизу, волосистые, позднее становящиеся голыми, появляются после цветения и отмирают осенью.\n" +
                            "\n" +
                            "Стебли прямостоящие, одетые густыми, оттопыренными, мягкими волосками.\n" +
                            "\n" +
                            "Листочки покрывала прямостоящие, разделённые на узколинейные доли, сильно волосистые. Цветоносы прямые; цветки прямостоящие, вначале ширококолокольчатые, позднее звездчато раскрытые; околоцветник простой, шестилистный, с листочками 3—4 см длиной, узкояйцевидно-заострёнными, прямыми, чаще сине-фиолетовыми, редко белыми или желтыми, снаружи волосистыми; тычинки многочисленные, во много раз короче листочков околоцветника, жёлтые, из них наружные превращены в стаминодии (медовики); пестиков много, с длинным пушистым столбиком 3—5 мм длиной. Цветёт в апреле — мае. Формула цветка: \n" +
                            "Плодики продолговатые; как и столбики, сильно волосистые." +
                            "\nРаспространение и экология\n" +
                            "\n" +
                            "Сон-трава. Бурятия\n" +
                            "Северная Европа: Финляндия (юг), Швеция (восток); Центральная Европа: Чешская Республика, Германия, Венгрия, Польша, Словакия; Южная Европа: Румыния; территория бывшего СССР: Беларусь, Эстония, Латвия, Литва, Европейская часть России, Украина, Казахстан, Западная Сибирь, Восточная Сибирь (юг), Дальний Восток; Азия: Китай, Монголия; Северная Америка: Канада (запад Северо-Западной Территории, Юкон, запад Онтарио, Альберта, Британская Колумбия, Манитоба, Саскачеван), США (север Иллинойса, Айова, Миннесота, Небраска, Северная Дакота, Южная Дакота, Висконсин, Колорадо, Айдахо, Монтана, Вашингтон, Вайоминг, Нью-Мексико, Техас, север Юты[3].\n" +
                            "Растёт на дёрново-подзолистой почве в сосновых, сосново-дубовых, сосново-берёзовых лесах верескового, брусничного, орлякового, мшистого и травяного типов, на вересковых пустошах, боровых склонах и в кустарниках[4].",
                    city = "СПБ",
                    views = 77,
                    rating = 4.1F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Сон-трава.JPG/1920px-Сон-трава.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/9/90/Anemone_spp_Sturm42.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Філія_ЛПЗ_НАНУ_%22Стрільцівський_степ%22_Pulsatilla_patens_%28ЧКУ%29.jpg/1920px-Філія_ЛПЗ_НАНУ_%22Стрільцівський_степ%22_Pulsatilla_patens_%28ЧКУ%29.jpg"
                    ),
                    mClass = "Лютикоцветные",
                    family = "Лютиковые",
                    taste = "Горьковатый",
                    genus = "Прострел",
                    isLiked = false
                ),
                HerbEntity(
                    id = 16,
                    name = "Калужница",
                    title = "Калу́жница (лат. Cáltha) \nНебольшой род многолетних травянистых растений семейства Лютиковые (Ranunculaceae), обитающих во влажных или заболоченных местах.\n" +
                            "\n" +
                            "Разные источники указывают количество от 3 до 40 видов. На территории бывшего СССР произрастает 6 видов[1]." +
                            "каждую весну зацветает по берегам канав, прудов и других водоёмов, в заболоченных лесах. Цветки у неё ярко-жёлтые, как и у многих других представителей семейства. Листья и стебли плотные, кожистые и блестящие. В тканях калужницы содержатся ядовитые вещества – алкалоиды. Уже в конце мая – начале июня у калужницы созревают семена. В Ленинградской области встречается три вида калужницы. Калужница болотная (C. palustris) – самый массовый. Два других вида – калужница укореняющаяся (C. radicans) и калужница рогатая (C. cornuta) – встречаются довольно редко. В европейских странах на альпийских горках часто культивируется калужница с махровыми цветками.",
                    description = "Стебель мясистый, прямостоячий (либо восходящий и приподнимающийся), реже — лежачий (в этом случае легко укореняется в узлах), облиственный, голый. Высота растения от 3 до 40 и более см.\n" +
                            "\n" +
                            "От короткого корневища, залегающего на глубине 2—3 см от поверхности почвы, отходят косо в низ многочисленные беловато-жёлтые придаточные корни. Во внутренней коре корневищ и корней хорошо развиты межклетники. На болотах глубина проникновения корней 25 см. Некоторые исследователи считают микотрофом, по другим данным микориза отсутствует[2].\n" +
                            "\n" +
                            "Листья очередные, цельные, почковидные или сердцевидные, по краю городчатые или городчато-зубчатые, тёмно-зелёные, голые, блестящие. Розеточные листья крупные (иногда до 20 см в поперечнике), на длинных мясистых черешках, стеблевые — значительно меньше, на коротких черешках. Прицветные листья сидячие. Формула цветка: \n" +
                            "Цветки числом до 7 расположены на длинных цветоносах в пазухах верхних листьев. Околоцветник простой, ярко-жёлтый, оранжевый или золотистый, до 5 см в диаметре, состоит из 5 листочков, длиной до 25 мм каждый. Тычинок много, пестиков от 2 до 12. В Европейской части России цветёт в апреле-мае.\n" +
                            "\n" +
                            "Плод — многолистовка. Число листовок соответствует числу пестиков в цветке. Листовки имеют на конце носик. Листовка содержит до 10 чёрных блестящих семян (размером до 2,5 мм), выпадающих из неё по созревании (в мае-июне).\n" +
                            "\n" +
                            "Число хромосом: 2n=32, 56, 60." +
                            "\nРаспространение и экология\n" +
                            "Распространена повсюду в зоне умеренного климата: в Европе (за исключением самой южной части) и в Закавказье, в Северной Америке (включая Аляску и Юкон), в Казахстане, Монголии и Японии, на севере и западе Китая, а также в горных районах Индийского субконтинента (север Индии, Бутан и Непал).\n" +
                            "\n" +
                            "В России произрастает повсеместно.\n" +
                            "\n" +
                            "Растёт в медленно текущей или стоячей воде вокруг родников и вдоль речек и ручьёв, в озёрах, на болотах и заболоченных участках в лесах и лугах, по сырым канавам. В горах забирается на высоту до 4 000 м над уровнем моря[4].\n" +
                            "\n" +
                            "Размножается исключительно семенами. По наблюдениям в 1948 году на лугах притеррасной части Оки в среднем один экземпляр давал 290 семян. В лучших условиях произрастания одно растение может давать до 2800 семян на одно растения. Вес 1000 семян около 8,5 грамм. Семена содержат развитую губчатую ткань, что обеспечивает возможность переноса их на значительные расстояния водой. Данные о всхожести противоречивы (от 2 до 99 %). Лучше прорастают на свету[2].\n" +
                            "\n" +
                            "Типичный гемикриптофит, на зиму надземные органы отмирают, а зимующие почки располагаются в близ поверхности[5][6].",
                    city = "СПБ",
                    views = 96,
                    rating = 4.2F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/2/21/Blatouch_bahení.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7c/Caltha_palustris_—_Flora_Batava_—_Volume_v1.jpg/1024px-Caltha_palustris_—_Flora_Batava_—_Volume_v1.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/5/54/Calthus-palustris-Knospe.jpg"
                    ),
                    mClass = "Лютикоцветные",
                    family = "Лютиковые",
                    taste = "Острый",
                    genus = "Калужница",
                    isLiked = false
                ),
                HerbEntity(
                    id = 17,
                    name = "Сурепка обыкновенная",
                    title = "Суре́пка обыкнове́нная[1], или Сурепица обыкновенная[2] (лат. Barbaréa vulgáris) — двулетнее травянистое растение; типовой вид рода Сурепка из семейства Капустные (Brassicaceae). Также встречается в литературе как Сурепица дуговидная по устаревшему синонимичному названию Barbarea arcuata[2].\n" +
                            "\n" +
                            "Распространена по всей Европе, в России — в европейской части и Западной Сибири; кроме того, была занесена в Северную Америку, Японию, Африку, Австралию и Новую Зеландию, таким образом превратившись в вид-космополит.\n" +
                            "Типичный сорняк, растущий на полях и в огородах, вдоль дорог. Как и у всех крестоцветных, у этих растений в тканях содержатся особые вещества – горькие гликозиды, придающие зелени характерный привкус. Кроме того, сурепку редко повреждают насекомые-вредители, так как в ней содержатся сапонины. Цветы сурепки часто посещают насекомые, так как это медоносное и перганосное растение (пергá – пыльца, собранная пчёлами с цветков растений, сложенная и утрамбованная в соты и залитая сверху мёдом). Помимо сурепки обыкновенной, растущей повсеместно, у нас произрастают и другие виды сурепки, однако они встречаются значительно реже.",
                    description = "Двулетнее, стержнекорневое растение высотой 30—80 см. Стебель высоковетвистый, голый или слегка пушистый.\n" +
                            "\n" +
                            "Прикорневые и нижние стеблевые листья на черешках с двумя—четырьмя продолговатыми боковыми долями и крупной, у основания сердцевидной, тупо-выемчато-зубчатой верхушечной долей. Верхние стеблевые листья сидячие, цельные, от ланцетных до обратнояйцевидных, по краю зубчатые.\n" +
                            "\n" +
                            "Соцветие — кисть, в начале цветения неразветвлённая. Цветки четырёхчленные с двойным околоцветником, обоеполые, золотисто-жёлтые. Лепестки длиной 5—7 мм, вдвое длиннее чашелистиков. В цветке шесть тычинок.\n" +
                            "\n" +
                            "Пыльцевые зёрна трёхбороздные, шаровидной или эллипсоидальной формы. Длина полярной оси 18,7—22,4 мкм, экваториальный диаметр 18,7—20,4 мкм. В очертании с полюса округло-трёхлопастные, с экватора — округлые или эллиптические. Борозды шириной 5—7 мкм, длинные, с неровными краями и притупленными концами; мембрана борозд зернистая. Ширина мезокольпиума 11,9—14 мкм, диаметр апокольпиума 3—4 мкм. В центре мезокольпиума экзина имеет толщину 1,2—1,8 мкм, около борозд она утончена за счёт стерженькового слоя. Стерженьки тонкие, на мезокольпиумах, высотой 0,8—1 мкм, с мелкими, округлыми головками, диаметром 0,2—0,3 мкм. Расстояние между стерженьками 0,7—1,2 мкм. Подстилающий слой тонкий. Скульптура мелкосетчатая, сетка разноячеистая, наибольший диаметр ячеек 1 мкм, наименьший (на полюсах) — не превышает 0,4 мкм. Пыльца ярко-жёлтого цвета.\n" +
                            "\n" +
                            "\n" +
                            "Семена сурепки обыкновенной\n" +
                            "Плод — стручок, продолговато-линейный, округло-четырёхгранный, с коротким булавовидным носиком, двустворчатый, двугнёздный, многосемянный. Створки стручка соломенно-жёлтые, твёрдые, голые, с ясной средней и малозаметными боковыми жилками; поверхность их слабобугорчатая. Плодоножки короткие, изогнуто-отклонённые, косо вверх направленные. Стручки вскрываются продольно снизу вверх двумя щелями с образованием створок, отделяющихся от срединной продольной перегородки.\n" +
                            "\n" +
                            "Семена овальные, сдавленные, серовато-коричневые со слабым блеском. Поверхность семян мелкобугорчатая.\n" +
                            "\n" +
                            "Число хромосом 2n=16." +
                            "\nЭкология и жизненный цикл\n" +
                            "Цветёт весной и ранним летом около месяца, плодоносит в июне—июле начиная со второго года вегетации. После периода плодоношения надземные части растения отмирают, новый цветущий и плодоносящий стебель развивается из корневой шейки каждую весну[источник не указан 473 дня].\n" +
                            "\n" +
                            "Размножается семенами и корневой порослью. Максимальная плодовитость — до 10 тыс. семян. Минимальная температура прорастания семян 6—8°С, максимальная — 38—40°С, оптимальная — 18—20°С. Семена прорастают быстро, летом, осенью и после перезимовки весной с глубины не более 4 см, лучше всего с глубины 0,5 см. В первый год жизни образуется только розетка листьев с хорошо развитым стержневым корнем, которая и перезимовывает.\n" +
                            "\n" +
                            "Отношение к влаге и почвенному плодородию: мезофит, мезотроф. Может произрастать в условиях полутени.\n" +
                            "\n" +
                            "Сорное растение преимущественно лесной зоны, на юге встречается реже, в местах избыточно увлажнённых. Как рудерал растёт на сырых лугах, вдоль рек, по лесным полянам, в зарослях кустарников, на вырубках, у дорог, по канавам, на залежах и мусорных местах. Сурепка обыкновенная обсеменяется уже в начале лета до уборки полевых культур и сильно засоряет почву.",
                    city = "СПБ",
                    views = 19,
                    rating = 3.6F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Barbarea_vulgaris_kz07.jpg/1024px-Barbarea_vulgaris_kz07.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Illustration_Barbaraea_vulgaris0.jpg/800px-Illustration_Barbaraea_vulgaris0.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Barbarea_vulgaris_kz05.jpg/1920px-Barbarea_vulgaris_kz05.jpg"
                    ),
                    mClass = "Капустоцветные",
                    family = "Капустные",
                    taste = "Острый",
                    genus = "Сурепка",
                    isLiked = false
                ),
                HerbEntity(
                    id = 18,
                    name = "Сердечник",
                    title = "Серде́чник лугово́й[1] (лат. Cardámine praténsis) — многолетнее травянистое растение, вид рода Сердечник (Cardamine) семейства Капустные (Brassicaceae). Ареал распространения охватывает обширные территории Северного полушария. Благодаря своим белым или нежно-фиолетовым цветам он часто доминирует в ландшафтах на богатых питательными веществами влажных лугах с конца апреля до середины мая.\n" +
                            "Растёт в сырых лесах, на заболоченных лугах, образуя куртины. Цветы белые или светло-фиолетовые, пыльники – жёлтые, фиолетовые. Семена горькие, листья тоже с горчинкой, так как содержат гликозиды. Весной молодые листья и стебли широко распространённого сердечника горького (C. amara) можно использовать для салатов. В Ленинградской области встречается несколько трудноразличимых видов сердечника, для некоторых из них (сердечник луговой (С. pratensis) и сердечник зубчатый (С. dentata)) характерно вегетативное размножение – во влажных условиях доли листьев могут укореняться. Два вида сердечника – жёстковолосистый (C. hirsuta) и мелкоцветковый (С. parviflora) внесены в Красную книгу Ленинградской области.",
                    description = "Сердечник (Cardamine) — род одно-, дву- или многолетних корневищных мезофитных трав семейства крестоцветных. Представители рода распространены по всему Земному шару, за исключением Антарктики. 31\n" +
                            "\n" +
                            "Растение достигает до 60 см в высоту, стебель может быть как прямостоячим, так и лежачим. Листья на стебле расположены очередно. Цветки белые, красные, лиловые или желтоватые, в кистевидных соцветиях. Плод — плоский стручок. 43\n" +
                            "\n" +
                            "Некоторые виды сердечника:\n" +
                            "\n" +
                            "Сердечник горький. На территории России распространён во многих районах европейской части и в Западной Сибири. Стебли прямые или восходящие, ребристые. Цветки белые, реже светло-сиреневые, в щитковидной кисти. Произрастает вблизи ручьёв. 2\n" +
                            "Сердечник недотрога. Растёт по берегам рек и ручьёв, в тенистых и влажных лесах, на глинистых и щебнистых склонах до среднегорного пояса. Применяют в народной медицине. 2\n" +
                            "Сердечник мелкоцветковый. Растение широко распространено в Евразии, Северной Африке, Северной Америке. Обычно встречается в сухой почве газонов, полей и лесных полян, как в тени, так и на солнце. 2\n" +
                            "Сердечник луговой. Растёт на сырых местах. В диком виде произрастает на всей территории Европы, в Северной Америке и Восточной Азии. На территории России встречается в европейской части, в Сибири, на Дальнем Востоке. 52\n" +
                            "Некоторые виды сердечника могут использоваться как медоносные, витаминные, пряносалатные или декоративные растения. 3",
                    city = "СПБ",
                    views = 29,
                    rating = 4.4F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Cardamine_appendiculata_1.JPG/1920px-Cardamine_appendiculata_1.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/b/b8/Cardamine_sp_Sturm13.jpg",
                    ),
                    mClass = "Капустоцветные",
                    family = "Капустные",
                    taste = "Острый",
                    genus = "Сердечник",
                    isLiked = false
                ),
                HerbEntity(
                    id = 19,
                    name = "Гусиный лук",
                    title = "Гусиный лук, или Гусинолук, или Гусятник, или Гейджия, или Птичий лук[источник не указан 934 дня] (лат. Gagea) — род травянистых луковичных растений семейства Лилейные (Liliaceae), распространённых в умеренных областях Евразии, а также в Северной Африке.\n" +
                            "\n" +
                            "Все представители рода — ранневесенние эфемероиды. Общее число видов — по данным сайта The Plant List — около двухсот[5].",
                    description = "Высота растений — от 3 до 35 см. У растений может быть одна луковица, но часто образуется также несколько дочерних луковиц, связанных с материнской луковицей столонами.\n" +
                            "\n" +
                            "У некоторых видов имеются корни двух типов: идущие от середины донца вертикально вниз (корни с положительным геотропизмом), и идущие от края донца сначала вниз, а затем горизонтально и вверх (корни с нулевым и отрицательным геотропизмом). Корни второго типа оплетают луковицу, образуя подобие защитной капсулы.\n" +
                            "\n" +
                            "Базальных листьев — один или два, они узкие, длинные, плоские, их высота обычно превышает высоту соцветия. Число листьев на цветоносах — от одного до десяти.\n" +
                            "\n" +
                            "Соцветия зонтиковидные извилины, с небольшим числом цветков, иногда даже редуцированы до одного цветка. Цветки небольшие, жёлтые, звёздчатые. Околоцветник простой, венчиковидный, состоит из шести листочков (сегментов), расположенных в два круга. Тычинок шесть. Опыление происходит с помощью насекомых, которых привлекает нектар, скапливающийся между основаниями тычиночных нитей и листочков околоцветника.\n" +
                            "\n" +
                            "Плод — коробочка.\n" +
                            "\n" +
                            "Вскоре после цветения надземная часть отмирает.\n" +
                            "\n" +
                            "Гусиные луки интенсивно размножаются и с помощью луковичек, которые образуются на днище луковицы, в пазухах базальных и стеблевых листьев, а иногда и на месте бутонов." +
                            "\nРод Гусиный лук, как и наиболее близкий к нему род Ллойдия (Lloydia), а также роды Тюльпан (Tulipa) и Эритрониум (Erythronium) входят в трибу Тюльпановые (Tulipeae), относящуюся к подсемейству Лилейные (Lilioideae) семейства Лилейные (Liliaceae)[7].\n" +
                            "\n" +
                            "Некоторые авторы выделяют роды Гусиный лук и Ллойдия в отдельную трибу Гейджиевые (Gageeae) в составе того же подсемейства[8][нет в источнике]. Иногда род Lloydia могут включать в род Гусиный лук[9].",
                    city = "СПБ",
                    views = 60,
                    rating = 4.9F,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Gagea_lutea_070406a.jpg/1280px-Gagea_lutea_070406a.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/3/3a/Gagea_villosa_Sturm30.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/Gagea_chlorantha_1.jpg/1024px-Gagea_chlorantha_1.jpg"
                    ),
                    mClass = "Капустоцветные",
                    family = "Капустные",
                    taste = "Острый",
                    genus = "Сердечник",
                    isLiked = false
                ),
            ))
        }

        val herbs = dao.getHerbs()
        emit(herbs)
    }

    fun getHerbById(id: Int): Flow<HerbEntity> = flow {
        emit(dao.getHerbById(id))
    }

    fun getElements(): Flow<List<ElementEntity>> = flow { 
        if (dao.getElements().isNullOrEmpty()) {
            dao.insertElements(listOf(
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_1), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_2), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_3), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_4), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_5), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_6), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_7), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_8), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_9), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_10), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_11), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_12), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_13), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_14), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_15), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_16), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_17), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_18), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_19), false),
                ElementEntity(id = 0, signId = 1, applicationContext.getString(R.string.class_20), false),
                
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_1), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_2), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_3), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_4), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_5), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_6), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_7), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_8), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_9), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_10), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_11), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_12), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_13), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_14), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_15), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_16), false),
                ElementEntity(id = 0, signId = 2, applicationContext.getString(R.string.genus_17), false),
                
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_1), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_2), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_3), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_4), false),
                ElementEntity(id = 0, signId = 3, applicationContext.getString(R.string.taste_5), false),

                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_1), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_2), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_3), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_4), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_5), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_6), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_7), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_8), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_9), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_10), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_11), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_12), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_13), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_14), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_15), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_16), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_17), false),
                ElementEntity(id = 0, signId = 4, applicationContext.getString(R.string.family_18), false),
            ))
        }

        val elements = dao.getElements()
        emit(elements)
    }

    suspend fun updateHerb(herbEntity: HerbEntity) {
        dao.updateHerb(herb = herbEntity)
    }

}