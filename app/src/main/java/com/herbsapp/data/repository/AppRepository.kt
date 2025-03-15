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
                    Resource.Failure(Exception("user is no login"))
                }
                Resource.Success("success")
            } else {
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
                    id = 1,
                    name = "Береза повислая",
                    title = "Береза повислая – символ русской природы, обладающая множеством полезных свойств",
                    description = """
            Береза повислая (Betula pendula) является одним из самых распространенных деревьев в России и символизирует русскую природу. 
            Она может достигать до 30 метров в высоту, образуя раскидистую крону с сердцевидными листьями, ширина которых может составлять до 7 см. 
            Листья имеют зубчатые края и выраженное сетчатое жилкование. 
            Береза предпочитает солнечные и полузатененные места, часто встречается на легких, хорошо осушенных почвах, таких как песчаники и суглинки. 
            Древесина березы используется в деревообработке для производства мебели, лыж и других изделий. 
            Кора березы славится своими лечебными свойствами, а ее сок, получаемый весной, богат витаминами и минералами. 
            Благодаря своим очищающим свойствам, береза используется в народной медицине для лечения различных заболеваний. 
            Кроме того, березовые леса служат местом обитания для многих видов животных и являются важным элементом экосистемы.
        """.trimIndent(),
                    city = "СПБ",
                    views = 45,
                    rating = 4.5f,
                    mClass = "Дерево",
                    family = "Березовые",
                    taste = "Неопределенный",
                    genus = "Betula",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/5/5e/Betula_Pendula_at_Stockholm_University_2005-07-01.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Illustration_Betula_pendula0.jpg/800px-Illustration_Betula_pendula0.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Betula_pendula_subsp_Fontqueri_textura_del_tronco.jpg/1024px-Betula_pendula_subsp_Fontqueri_textura_del_tronco.jpg"
                    ),
                    veining = "Сетчатое",
                    shape = "Сердцевидная"
                ),
                HerbEntity(
                    id = 2,
                    name = "Сосна обыкновенная",
                    title = "Сосна обыкновенная – надежный защитник лесов и ценная древесина",
                    description = """
            Сосна обыкновенная (Pinus sylvestris) – это высокое хвойное дерево, достигающее высоты до 40 метров, с характерной корой, которая имеет красноватый оттенок. 
            Листья сосны, собранные в пучки по два, имеют узкую игловидную форму длиной около 5-10 см и параллельное жилкование. 
            Сосна предпочитает солнечные участки, но хорошо растет и в полутени, на различных типах почв, от песчаных до более тяжелых. 
            Древесина обладает высокой прочностью и стойкостью к гниению, что делает ее идеальной для строительства и производства мебели. 
            Смола сосны содержит ряд полезных веществ, что позволяет использовать ее в народной медицине для лечения различных недугов. 
            Кроме того, сосновые леса улучшают качество воздуха, обогащая его кислородом и фитонцидами. 
            Они служат местом обитания для множества животных и растений, играя важную роль в экосистеме, а их хвойный аромат оказывает благоприятное воздействие на здоровье человека.
        """.trimIndent(),
                    city = "СПБ",
                    views = 65,
                    rating = 4.7f,
                    mClass = "Дерево",
                    family = "Сосновые",
                    taste = "Неопределенный",
                    genus = "Pinus",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/0/04/Сосна-России.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/12/Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_7%29_%286972231024%29.jpg/1024px-Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mит_кюр_erläuterndem_Texte_%28Plate_7%29_%286972231024%29.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Pinus_sylvestris_flos_pollen_bialowieza_forest_beentree.jpg/1920px-Pinus_sylvestris_flos_pollen_bialowieza_forest_beentree.jpg"
                    ),
                    veining = "Параллельное",
                    shape = "Ланцетная"
                ),
                HerbEntity(
                    id = 3,
                    name = "Липа сердцевидная",
                    title = "Липа сердцевидная – символ любви и здоровья, обладающая волшебными свойствами",
                    description = """
            Липа сердцевидная (Tilia cordata) – это древо, которое в славянской мифологии считается символом любви и семейного счастья. 
            Высота дерева может достигать 30 метров, а его крону отличают широкие, сердцевидные листья, длиной 4-10 см с крупными, зубчатыми краями и выраженным сетчатым жилкованием. 
            Липа предпочитает плодородные, влажные почвы и солнечные места, однако также способна расти в полутени. 
            Цветки липы распускаются в июне и обладают сладковатым ароматом, привлекая множество опылителей, таких как пчелы и бабочки. 
            Липа широко используется в народной медицине благодаря своим антисептическим и противовоспалительным свойствам, а ее цветы, листья и кора применяются для приготовления настоев и чаев. 
            Древесина липы легкая и мягкая, что делает ее идеальной для резьбы и производства музыкальных инструментов. 
            Липовые дупла служат домом для многих птиц и мелких животных, а также создают тень и облагораживают окружающую местность.
        """.trimIndent(),
                    city = "СПБ",
                    views = 30,
                    rating = 4.6f,
                    mClass = "Дерево",
                    family = "Липовые",
                    taste = "Сладкий",
                    genus = "Tilia",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Tilia_cordata_001.jpg/1024px-Tilia_cordata_001.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mит_кюр_erläuterndem_Texte_%28Plate_15%29_%287118312783%29.jpg/1024px-Köhler%27s_Mедизинал_Планцен-в_натургадреукунн-Аббиду-мит_тексте_%28Plate_15%29_%287118312783%29.jpg"
                    ),
                    veining = "Сетчатое",
                    shape = "Сердцевидная"
                ),
                HerbEntity(
                    id = 4,
                    name = "Клен красивый",
                    title = "Клен красивый – яркий символ осени и национальный природный памятник",
                    description = """
            Клен красивый (Acer platanoides) – это лиственное дерево, вырастающее до 30 метров, известное своими яркими осенними красками. 
            Листья клена большие, пятилопастные, с выразительным жилкованием и достигают до 15 см в диаметре. 
            Дерево предпочитает солнечные места и богатые влажные почвы, что позволяет ему легко укореняться в городских условиях. 
            Клен красивый не только является декоративным элементом, придающим паркам и скверам особую атмосферу, но и производит кислород, улучшая качество воздуха. 
            Его древесина ценится за прочность и цвет, применяется в производстве мебели, музыкальных инструментов и даже в спортивных товарах. 
            Кроме того, кленовые семена, известные как «виноградки», созревают в конце лета и являются пищей для многих птиц. 
            Клен обладает великолепными декоративными свойствами, особенно в осенний период, когда его листья приобретают яркие желтые и красные оттенки.
        """.trimIndent(),
                    city = "СПБ",
                    views = 50,
                    rating = 4.5f,
                    mClass = "Дерево",
                    family = "Кленовые",
                    taste = "Неопределенный",
                    genus = "Acer",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Acer_pseudoplatanus_005.jpg/1920px-Acer_pseudoplatanus_005.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Acer_platanoides_1.JPG/1920px-Acer_platanoides_1.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Maple-oliv1.jpg/1920px-Maple-oliv1.jpg"
                    ),
                    veining = "Пальчатое",
                    shape = "Пальчатая"
                ),
                HerbEntity(
                    id = 5,
                    name = "Шиповник коричный",
                    title = "Шиповник коричный – волшебный кустарник с лечебными свойствами",
                    description = """
            Шиповник коричный (Rosa cinnamomea) – это один из любимых кустарников садоводов и любителей дикой природы, который можно встретить в СПБ. 
            Он представляет собой не высокий кустарник, достигающий в высоту до 2 метров и обладающий продолговатыми листьями с зубчатыми краями. 
            Листья имеют четкое жилкование и сильно пахнущие розовыми цветами, которые распускаются в зависимости от условий погоды в конце весны и начале лета. 
            Шиповник предпочитает солнечные места, растет на открытых пространствах и лесных опушках, часто образуя густые заросли. 
            Плоды шиповника имеют высокое содержание витамина C и используются для приготовления различных настоек, чаев и варений, а также в народной медицине для укрепления иммунной системы. 
            Кустарник привлекает множество пчел и бабочек, а также служит хорошим укрытием для мелких животных и птиц, способствуя поддержанию экосистемы.
        """.trimIndent(),
                    city = "СПБ",
                    views = 40,
                    rating = 4.3f,
                    mClass = "Кустарник",
                    family = "Розовые",
                    taste = "Сладкий",
                    genus = "Rosa",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/8/82/Rosa-majalis-habit.JPG",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/6/61/Illustration_Rosa_majalis0.jpg/800px-Illustration_Rosa_majalis0.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Rosa_majalis.jpg/1920px-Rosa_majalis.jpg"
                    ),
                    veining = "Сетчатое",
                    shape = "Округлая"
                ),
                HerbEntity(
                    id = 6,
                    name = "Орешник обыкновенный",
                    title = "Орешник обыкновенный – символ изобилия и источники витаминов",
                    description = """
            Орешник обыкновенный (Corylus avellana) – это многолетний кустарник, который часто можно встретить в лесах и парках СПБ. 
            Он способен достигать высоты до 5 метров и выделяется округлыми, крупными листьями, которые имеют мелкие зубцы и выраженное сетчатое жилкование. 
            Орешник требует достаточно света и предпочитает полутенистые участки с плодородными,湿ными почвами. 
            Важно отметить, что его плоды – орехи – являются настоящим источником витаминов и минералов, особенно витамина C и E, а также полезных жиров, что делает их популярной закуской и ингредиентом в кулинарии. 
            Орешник цветет ранней весной, его серебристые серёжки становятся настоящей гармонией весны. 
            Кроме того, орешник является важным элементом экосистемы, поскольку служит источником пищи для птиц и мелких млекопитающих. 
            Благодаря своей стойкости к болезням и неприхотливости, орешник находит свое место в садоводстве и ландшафтном дизайне.
        """.trimIndent(),
                    city = "СПБ",
                    views = 35,
                    rating = 4.2f,
                    mClass = "Кустарник",
                    family = "Березовые",
                    taste = "Ореховый",
                    genus = "Corylus",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Corylus_avellana_shrub.jpg/1920px-Corylus_avellana_shrub.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Corylus_avellana_female_flower_-_Keila.jpg/1024px-Corylus_avellana_female_flower_-_Keila.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Illustration_Corylus_avellana0_clean.jpg/800px-Illustration_Corylus_avellana0_clean.jpg"
                    ),
                    veining = "Сетчатое",
                    shape = "Округлая"
                ),
                HerbEntity(
                    id = 7,
                    name = "Мелисса лимонная",
                    title = "Мелисса лимонная – веселая трава с ароматом и лечебными свойствами",
                    description = """
            Мелисса лимонная (Melissa officinalis) – это многолетнее травянистое растение, которое активно используется в кулинарии и медицине. 
            Она известна своими ярко-зелеными, овальными листьями, которые имеют зубчатые края и выраженное жилкование. 
            Мелисса достигает высоты 70 см и предпочитает солнечные места с влажными, хорошо дренированными почвами, что делает её отличным кандидатом для огорода или сада. 
            Однако, к её ароматным свойствам стоит отнести легкий лимонный запах, что делает растения незаменимыми в чаях, десертах и даже как привлекательный элемент в ароматерапии. 
            Мелисса помогает успокоить нервную систему, и обладает многими полезными свойствами, облегчая бессонницу и улучшая пищеварение. 
            В народной медицине ее используют для приготовления настоев и чая для улучшения сна и снятия стресса, благодаря чему она стала популярным растением среди натуропатов и любителей фитотерапии.
        """.trimIndent(),
                    city = "СПБ",
                    views = 60,
                    rating = 4.6f,
                    mClass = "Трава",
                    family = "Яснотковые",
                    taste = "Лимонный",
                    genus = "Melissa",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/9/90/Melissa_officinalis.jpg",
                        "https://semena-zakaz.ru/upload/iblock/2fc/73ob469d60u8ycvs8ir07luqgn81jsyi/54ab83fe_be2f_11e7_8477_9c5c8ebecd88_ef73c5d8_76b3_11ea_b288_20e6170a5f15.resize2.jpg"
                    ),
                    veining = "Сетчатое",
                    shape = "Округлая"
                ),
                HerbEntity(
                    id = 8,
                    name = "Зверобой травянистый",
                    title = "Зверобой травянистый – защитник от невзгод и природный лекарь",
                    description = """
            Зверобой травянистый (Hypericum perforatum) – это известное многолетнее травянистое растение, которое широко используется в народной медицине. 
            Его листья имеют мелкую овальную форму, с характерными точками и выраженным жилкованием, что делает их легко узнаваемыми. 
            У зверобоя крепкие стебли, высота которых может достигать 80 см. Он предпочитает солнечные места и типичные для Европы луга и опушки. 
            Цветы, ярко-желтого окраса, распускаются в начале лета и используются для приготовления настоев, мазей и чая с уникальными целебными свойствами. 
            Зверобой известен своими антидепрессивными свойства и применяется для лечения различных психических заболеваний. 
            Растение также обладает противовоспалительными и антисептическими свойствами, что делает его идеальным средством для лечения кожи, особенно при дерматите и экземе. 
            Кроме того, зверобой является важным элементом экосистемы, поддерживая здоровье почвы и привлекая полезных насекомых, таких как пчелы.
        """.trimIndent(),
                    city = "СПБ",
                    views = 22,
                    rating = 4.4f,
                    mClass = "Трава",
                    family = "Зверобойные",
                    taste = "Горький",
                    genus = "Hypericum",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Hypericum_perforatum_Habitus_DehesaBoyalPuertollano.jpg/1024px-Hypericum_perforatum_Habitus_DehesaBoyalPuertollano.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/7/7c/Hypericum_spp_Sturm59.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/b/b0/Hypericum_calycinum_Tasmania.jpg"
                    ),
                    veining = "Перистое",
                    shape = "Ланцетная"
                ),
                HerbEntity(
                    id = 9,
                    name = "Ива белая",
                    title = "Ива белая – дерево с изысканной красотой и полезными качествами",
                    description = """
            Ива белая (Salix alba) – это быстрорастущее дерево, которое можно встретить вдоль рек и водоемов в СПБ. 
            Высота дерева может достигать 30 метров, а его листья длинные и узкие, с гладкой поверхностью и обратным жилкованием. 
            Это дерево предпочитает влажные участки и светлые, солнечные места, и часто используется для укрепления берегов. 
            Ива белая знаменита своими пленительными серыми и желтыми серёжками, которые распускаются весной, привлекая внимание всей весенней флоры. 
            Листья ивы изготавливаются в народной медицине для приготовления отваров, обладающих противовоспалительными и обезболивающими свойствами, которые могут помочь при лечении различных недугов, включая головные боли и ревматизм. 
            Древесина ивы также используется для производства мебели и плетеных изделий, благодаря ее гибкости и прочности. 
            Ива белая предоставляет укрытие и источники пищи для множества животных, включая птиц и мелких млекопитающих.
        """.trimIndent(),
                    city = "СПБ",
                    views = 75,
                    rating = 4.1f,
                    mClass = "Дерево",
                    family = "Ивовые",
                    taste = "Неопределенный",
                    genus = "Salix",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Salix_alba_004.jpg/1920px-Salix_alba_004.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Salix_alba_022.jpg/1024px-Salix_alba_022.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Salix_alba_012.jpg/1920px-Salix_alba_012.jpg"
                    ),
                    veining = "Параллельное",
                    shape = "Ланцетная"
                ),
                HerbEntity(
                    id = 10,
                    name = "Меланжевик обыкновенный",
                    title = "Меланжевик обыкновенный – трава, знакомая всем с детства и обладающая высокой питательностью",
                    description = """
            Меланжевик обыкновенный (Plantago lanceolata) – это травянистое многолетнее растение, часто встречающееся по лугам и пустошах СПБ. 
            Высота меланжевика может достигать 40 см, а его листья узкие, продолговатые, с четко выраженным параллельным жилкованием. 
            Эта трава предпочитает солнечные места и легкие, песчаные или суглинковые почвы. 
            Меланжевик является популярной травой среди фитотерапевтов, так как его листья богаты витаминами, минералами и антиоксидантами. 
            В народной медицине его используют для лечения простудных заболеваний, кашля и для укрепления иммунной системы. 
            Цветы меланжевика имеют высокое содержание нектаров и привлекают множество насекомых, а их мягкий аромат делает растение особенно популярным среди пчел. 
            Таким образом, меланжевик обыкновенный играет важную роль в экосистеме, поддерживая устойчивое пчелиное сообщество.
        """.trimIndent(),
                    city = "СПБ",
                    views = 25,
                    rating = 3.8f,
                    mClass = "Трава",
                    family = "Подорожниковые",
                    taste = "Неопределенный",
                    genus = "Plantago",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/1/17/Ribwort_600.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/6/63/Plantago_lanceolata_Nordens_Flora.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/5/5f/Plantago_lanceolata6_ies.jpg"
                    ),
                    veining = "Параллельное",
                    shape = "Ланцетная"
                ),
                HerbEntity(
                    id = 11,
                    name = "Лавр благородный",
                    title = "Лавр благородный – дерево с выдающимся ароматом и разнообразными полезными свойствами",
                    description = """
            Лавр благородный (Laurus nobilis) – это вечнозеленое дерево, известное своими листьями и уникальным ароматом, который будто переносит нас в средиземноморские страны. 
            Высота растения может достигать 10 метров, а его листья овальные, гладкие и изящно оформленные. 
            Лавр благородный предпочитает теплые и солнечные места с хорошо дренированными почвами и является устойчивым к засухе. 
            Листья лавра активно используются в кулинарии для придания блюдам особого аромата, а также в народной медицине для лечения различных заболеваний, благодаря своим противовоспалительным и антисептическим свойствам. 
            Эфирные масла, содержащиеся в лавровых листьях, способствуют улучшению пищеварения и укрепляют иммунную систему. 
            Также лавр символизирует победу и славу, его ветви традиционно используются для создания лавровых венков. 
            Эта культура также привлекает различные полезные насекомые и способствует сохранению биоразнообразия в своих экосистемах.
        """.trimIndent(),
                    city = "СПБ",
                    views = 40,
                    rating = 4.7f,
                    mClass = "Кустарник",
                    family = "Лавровые",
                    taste = "Пряный",
                    genus = "Laurus",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Laurus_nobilis_Laurel_tree.jpg/1024px-Laurus_nobilis_Laurel_tree.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/4/42/Laurus_nobilis_-_Köhler–s_Medizinal-Pflanzen-086.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/e/ec/Laurus_nobilis_flowers_1.jpg"
                    ),
                    veining = "Сетчатое",
                    shape = "Округлая"
                ),
                HerbEntity(
                    id = 12,
                    name = "Смородина черная",
                    title = "Смородина черная – царь ягод, привносящий витамины в наш рацион",
                    description = """
            Смородина черная (Ribes nigrum) – это кустарник, известный своими темными, ароматными ягодами и рядом полезных свойств. 
            Высота данного кустарника достигает 1-1,5 метра, а его листья крупные, тёмно-зеленого цвета, с выразительным жилкованием и характерными зубцами по краям. 
            Смородина предпочитает солнечные места и влажные, плодородные почвы, что делает ее идеальной для посадки в садах и огородах. 
            Ягоды смородины являются богатым источником витамина C, антиоксидантов, а также других витаминов и минералов, которые способствуют укреплению иммунной системы и улучшению общего состояния здоровья. 
            Ягоды можно использовать для приготовления варений, джемов, морсов, а также в народной медицине для антиоксидантных и противовоспалительных целей. 
            Смородина также привлекает полезных насекомых и птиц, играя важную роль в экосистеме. 
            Кроме того, ее можно использовать для ландшафтного дизайна, ведь соцветия и ягоды радуют глаз не только летом, но и осенью.
        """.trimIndent(),
                    city = "СПБ",
                    views = 50,
                    rating = 4.6f,
                    mClass = "Кустарник",
                    family = "Гроздевые",
                    taste = "Сладкий",
                    genus = "Ribes",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Blackcurrant_1.jpg/1024px-Blackcurrant_1.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/5/56/281_Ribes_nigrum.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/c/cb/Ribes-nigrum.JPG"
                    ),
                    veining = "Сетчатое",
                    shape = "Округлая"
                ),
                HerbEntity(
                    id = 13,
                    name = "Жостер черный",
                    title = "Жостер черный – крепость природы с чудесными свойствами",
                    description = """
            Жостер черный (Rhamnus catharticus) – это кустарник, часто встречающийся вдоль рек и на опушках лесов. 
            Он может достигать в высоту до 3 метров и выделяется овальными листьями с гладкой поверхностью и мелкими зубцами по краям. 
            Жостер предпочитает солнечные или чуть затененные места, и часто образует плотные заросли, создавая укрытие для мелких животных. 
            Плоды жостера используются в народной медицине для приготовления настоев, применяемых в качестве слабительных и очищающих средств. 
            Благодаря своим свойствам, жостер черный также полезен для улучшения состояния кожи. 
            У этого растения нет характерного запаха, однако оно играет важную экосистемную роль, служа пищей для многих птиц и мелких млекопитающих. 
            Также его древесина используется для изготовления различных предметов, таких как решетки и мебель, что делает жостер важным элементом как в природе, так и в жизни человека.
        """.trimIndent(),
                    city = "СПБ",
                    views = 20,
                    rating = 4.4f,
                    mClass = "Кустарник",
                    family = "Жостеровые",
                    taste = "Неопределенный",
                    genus = "Rhamnus",
                    isLiked = false,
                    imageURL = listOf(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Rhamnus_catharthica_tree_size.jpg/1920px-Rhamnus_catharthica_tree_size.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_63%29_BHL303692.jpg/1024px-Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_63%29_BHL303692.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/7/70/Rhamnus_cathartica_5456085.jpg"
                    ),
        veining = "Сетчатое",
        shape = "Округлая"
    ),
    HerbEntity(
        id = 14,
        name = "Калина обыкновенная",
        title = "Калина обыкновенная – растение с целебными свойствами, символизирующее родину",
        description = """
            Калина обыкновенная (Viburnum opulus) – это кустарник, известный своими полезными ягодами и медицинскими свойствами.
            Он достигает высоты до 4 метров с крупными округлыми листьями, имеющими сильно зубчатые края и выраженное жилкование.
            Калина предпочитает влажные места и часто растет вблизи водоемов, что делает ее прекрасной декоративной растительностью для садов и парков.
            Цветы калины образуют декоративные белые соцветия, источающие сладкий запах, которые привлекают много пчел и насекомых.
            Ягоды калины известны своими целебными свойствами и используются в народной медицине для лечения различных заболеваний, включая простуды и заболевания печени.
            Также калина очень популярна в кулинарии для приготовления варений, соков и настоек.
            Этот кустарник имеет символическое значение в русской культуре, воплощая родину и мудрость народа, что делает его важным элементом не только в экосистеме, но и в культурной жизни общества.
            """.trimIndent(),
        city = "СПБ",
        views = 28,
        rating = 4.3f,
        mClass = "Кустарник",
        family = "Жимолостные",
        taste = "Сладкий",
        genus = "Viburnum",
        isLiked = false,
        imageURL = listOf(
            "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Viburnum_opulus_001.JPG/1024px-Viburnum_opulus_001.JPG",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Illustration_Viburnum_opulus0.jpg/800px-Illustration_Viburnum_opulus0.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/3/38/Viburnum_opulus20090612_434.jpg"
        ),
        veining = "Сетчатое",
        shape = "Округлая"
    ),
    HerbEntity(
        id = 15,
        name = "Душица обыкновенная",
        title = "Душица обыкновенная – кулинарная прелесть с множеством полезных свойств",
        description = """
            Душица обыкновенная (Origanum vulgare) – это многолетнее травянистое растение, активно используемое в кулинарии и медицине.
            Это растение достигает 30-60 см в высоту и имеет характерные овальные листья с мелкими зубчиками и ярким зеленым цветом.
            Душица предпочитает солнечные участки и хорошо дренированные почвы, что делает ее идеальным растением для открытия в садах.
            Листья душицы обладают насыщенным пряным ароматом, который делает ее известным ингредиентом для приготовления блюд, особенно в итальянской и греческой кухне.
            В народной медицине душицу используют для приготовления настоев, помогающих при респираторных заболеваниях, а также эффективных средств против укачивания.
            Кроме того, душица известна своими антиоксидантными и противовоспалительными эффектами.
            Это растение также привлекает полезных насекомых, таких как пчелы, что делает его важным элементом в садоводстве и поддержании здоровья экосистемы.
            """.trimIndent(),
        city = "СПБ",
        views = 55,
        rating = 4.8f,
        mClass = "Трава",
        family = "Яснотковые",
        taste = "Ароматный",
        genus = "Origanum",
        isLiked = false,
        imageURL = listOf(
            "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Origanum_vulgare_-_harilik_pune.jpg/1024px-Origanum_vulgare_-_harilik_pune.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/c/ca/Origanum_vulgare3_ies.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/5/5f/Origanum_vulgare_05_ies.jpg"
        ),
        veining = "Сетчатое",
        shape = "Округлая"
    ),
    HerbEntity(
        id = 16,
        name = "Фиалка душистая",
        title = "Фиалка душистая – ароматная весеннее счастье и народный лекарь",
        description = """
            Фиалка душистая (Viola odorata) – это многолетнее травянистое растение, часто встречающееся в садах и парках СПБ.
            Высота фиалки колеблется от 15 до 25 см, а ее листья имеют сердцевидную форму с гладкой поверхностью и четко выраженным жилкованием.
            Цветы фиалки, обычно фиолетового или белого цвета, распускаются в ранней весной и обладают сладким, приятным ароматом.
            Фиалки предпочитают полутеневые участки, при этом хорошо растут на влажных и питательных почвах.
            Цветы фиалки используют в кулинарии для приготовления десертов и настоев, а в медицине – для лечения простуды и улучшения пищеварения.
            Благодаря своим успокаивающим свойствам, фиалка становится популярным растением в ароматерапии.
            Фиалка также выступает важным элементом экосистемы, привлекая пчел и других опылителей, а её кошачьи листья иногда становятся местом обитания для разных мелких существ.
            """.trimIndent(),
        city = "СПБ",
        views = 39,
        rating = 4.5f,
        mClass = "Трава",
        family = "Фиалковые",
        taste = "Сладкий",
        genus = "Viola",
        isLiked = false,
        imageURL = listOf(
            "https://upload.wikimedia.org/wikipedia/commons/7/7a/Viola_odorata_fg01.JPG",
            "https://upload.wikimedia.org/wikipedia/commons/f/f0/ViennaDioscoridesFolio148vViolet.jpg"
        ),
        veining = "Сетчатое",
        shape = "Сердцевидная"
    ),
    HerbEntity(
        id = 17,
        name = "Лаванда узколистная",
        title = "Лаванда узколистная – душистое растение с пряным ароматом",
        description = """
            Лаванда узколистная (Lavandula angustifolia) – это многолетнее растение с вытянутыми, узкими листьями и характерным сине-фиолетовым цветением.
            Высота растений, как правило, колеблется от 20 до 60 см. Лаванда бывает разного размера, но все виды предпочитают солнечные и защищенные места с хорошо дренированными почвами.
            Аромат лаванды, исходящий от цветущих побегов в летний период, широко известен своим расслабляющим эффектом и используется в косметической и парфюмерной промышленности.
            Лаванда также имеет противовоспалительные и антиоксидантные свойства, что делает её популярной в натуральной медицине для улучшения работы нервной системы и снятия стресса.
            Цветы лаванды используются для приготовления чая, а также в кулинарии для ароматизации различных блюд.
            Растение также служит полезным атрибутом ландшафтного дизайна, постоянной составляющей низких, сложенных горизонтов, сохраняя контуры.
            """.trimIndent(),
        city = "СПБ",
        views = 34,
        rating = 4.6f,
        mClass = "Трава",
        family = "Яснотковые",
        taste = "Ароматный",
        genus = "Lavandula",
        isLiked = false,
        imageURL = listOf(
            "https://upload.wikimedia.org/wikipedia/commons/9/93/Lavandula-angustifolia-flowering.JPG",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_60%29_%286972249268%29.jpg/1024px-Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_60%29_%286972249268%29.jpg"
        ),
            veining = "Сетчатое",
            shape = "Ланцетная"
            ),
            HerbEntity(
                id = 18,
                name = "Татарская черемуха",
                title = "Татарская черемуха – искусительница весны и лечительница природы",
                description = """
            Татарская черемуха (Prunus padus) – это дерево или кустарник, известный своими белоснежными цветами, которые распускаются весной. 
            Высота черемухи колеблется от 3 до 6 метров, а ее листья ярко-зеленые, овальной формы с зубчатыми краями и ясно выраженным жилкованием. 
            Растение предпочитает влажные и солнечные места, часто обитает у водоемов. 
            Цветы черемухи образуют густые, декоративные соцветия, источающие сладкий аромат, что привлекает пчел и других опылителей. 
            Плоды черемухи имеют высокое содержание полезных веществ и используются для приготовления джемов и варений, а также в народной медицине для ускорения заживления ран и улучшения общего состояния здоровья. 
            Древесина татарской черемухи, прочная и долговечная, используется для производства мебели и предметов быта. 
            Этот кустарник не только приносит аромат и красоту в наши жизни, но и помогает поддерживать разнообразие в экосистемах.
        """.trimIndent(),
                city = "СПБ",
                views = 48,
                rating = 4.4f,
                mClass = "Дерево",
                family = "Розовые",
                taste = "Сладкий",
                genus = "Prunus",
                isLiked = false,
                imageURL = listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Baum_Mälardalen.jpg/1024px-Baum_Mälardalen.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/1/1d/312_Prunus_padus.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Ab_plant_1234.jpg/1920px-Ab_plant_1234.jpg"
                ),
                veining = "Сетчатое",
                shape = "Округлая"
            ),
            HerbEntity(
                id = 19,
                name = "Плодовая рябина",
                title = "Плодовая рябина – символ плодородия и источник полезных витаминов",
                description = """
            Плодовая рябина (Sorbus aucuparia) – это высокое дерево, достигающее до 15 метров в высоту, с перистыми листьями и яркими, красными ягодами, которые созревают в конце лета. 
            Листья рябины имеют 11-15 долей и четкое жилкование, образуя красивую крону. 
            Рябина предпочитает открытые солнечные места и хорошо дренированные почвы, поэтому часто встречается в парках и лесах СПБ. 
            Цветы рябины белые, собраны в соцветия и обладают легким сладким ароматом, что делает это дерево особенно привлекательным для опылителей. 
            Ягоды рябины богаты витамином C и используются для приготовления варений, настоек и соков; они также обладают полезными свойствами для иммунной системы. 
            Это дерево привлекает множество птиц, которые используют ягоды в качестве источника пищи, а также служит укрытием для мелких животных. 
            Рябина также широко используется в ландшафтном дизайне благодаря своей красоте и декоративности, делая её важным элементом для поддержания биоразнообразия.
        """.trimIndent(),
                city = "СПБ",
                views = 27,
                rating = 3.7f,
                mClass = "Дерево",
                family = "Рябиновые",
                taste = "Сладкий",
                genus = "Sorbus",
                isLiked = false,
                imageURL = listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Rowan_tree.jpg/1024px-Rowan_tree.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/Illustration_Sorbus_aucuparia0.jpg/1024px-Illustration_Sorbus_aucuparia0.jpg",
                    "https://upload.wikimedia.org/wikipedia/ru/2/2c/Рябина-лист-лицевая-сторона.jpg"
                ),
                veining = "Перистое",
                shape = "Пальчатая"
            ),
            HerbEntity(
                id = 20,
                name = "Тернослив",
                title = "Тернослив – дар природы с сочным вкусом и удивительными свойствами",
                description = """
            Тернослив (Prunus spinosa) – это низкорослый кустарник, который часто встречается в дикой природе, в лесах и на окраинах полей идеального для нашего региона. 
            Максимальная высота растения составляет около 3 метров, а его листья имеют гладкую, продолговатую форму и выраженные жилки. 
            Тернослив предпочитает солнечные участки и легкие почвы, образуя плотные заросли, благодаря чему он служит хорошим укрытием для мелких животных и птиц. 
            Цветы тернослива белые и источают сладковатый аромат, они распускаются в начале весны и становятся привлекательными для многих насекомых. 
            Плоды тернослива - небольшие синие ягоды - являются отличным источником витаминов и используются для приготовления варений, компотов и настоек. 
            Кроме того, тернослив активно используется в народной медицине для улучшения пищеварения и лечения заболеваний почек. 
            Устойчивость к морозам и неприхотливость делает тернослив популярным элементом ландшафтных дизайнов.
        """.trimIndent(),
                city = "СПБ",
                views = 18,
                rating = 4.4f,
                mClass = "Кустарник",
                family = "Сливовые",
                taste = "Сладкий",
                genus = "Prunus",
                isLiked = false,
                imageURL = listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/8/8f/Damson_plum_fruit.jpg"
                ),
                veining = "Сетчатое",
                shape = "Ланцетная"
            ),
            HerbEntity(
                id = 21,
                name = "Череда трехраздельная",
                title = "Череда трехраздельная – лекарь лугов и болот с уникальными свойствами",
                description = """
            Череда трехраздельная (Bidens tripartita) – это многолетнее травянистое растение, которое активно растет в болотистых местностях и на сырых лугах СПБ. 
            Высота растения достигает примерно 60 см, а его листья тройные, с заметным жилкованием, что делает череду легко узнаваемой среди других растений. 
            Череда предпочитает солнечные и полутенистые участки и хорошо адаптируется к различным условиям. 
            Растение цветет желтыми цветами, которые являются отличным источником нектара для пчел и других насекомых-опылителей. 
            Череда имеет множество целебных свойств: её используют в народной медицине для приготовления настоев и отваров, помогающих при заболеваниях печени и дыхательных путей. 
            Кроме того, череда имеет умеренные противовоспалительные свойства, что делает её ценным продуктом как в кулинарии, так и в фитотерапии. 
            Это растение также благоприятно влияет на окружающую среду, улучшая качество почвы и поддерживая разнообразие экосистемы.
        """.trimIndent(),
                city = "СПБ",
                views = 33,
                rating = 4.0f,
                mClass = "Трава",
                family = "Сложноцветные",
                taste = "Горький",
                genus = "Bidens",
                isLiked = false,
                imageURL = listOf(
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Bidens_tripartita1_eF.jpg/1024px-Bidens_tripartita1_eF.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/1/1d/Bidens_tripartita.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/6/61/Bidens_tripartita_MHNT.BOT.2004.0.774.jpg/1920px-Bidens_tripartita_MHNT.BOT.2004.0.774.jpg"
                ),
                veining = "Перистое",
                shape = "Ланцетная"
            ),
            HerbEntity(
            id = 22,
            name = "Фиалка трехцветная",
            title = "Фиалка трехцветная – волшебное растение, объединяющее красоту и полезные свойства",
            description = """
            Фиалка трехцветная (Viola tricolor) – это известное травянистое растение, которое радует глаз своим ярким цветением – сочетанием фиолетового, желтого и белого. 
            Она вырастает до 20 см в высоту, с листьями в форме сердечка и выраженными жилками. 
            Фиалка предпочитает солнечные и полутенистые места, растет на богатых, влажных почвах, что делает ее идеальной для цветочной клумбы. 
            Цветы фиалки не только красивы, но и обладают полезными свойствами – в народной медицине их используют для приготовления настоев, полезных при простудах и заболеваниях дыхательной системы. 
            Также их применяют для украшения десертов и напитков благодаря приятному вкусу и аромату. 
            Фиалка привлекает много пчел и бабочек, играя важную роль в экосистеме. 
            Эта культура может стать отличным дополнением к любому саду, придавая ему яркий и веселый вид, а также обогащая почву.
        """.trimIndent(),
            city = "СПБ",
            views = 46,
            rating = 4.5f,
            mClass = "Трава",
            family = "Фиалковые",
            taste = "Сладкий",
            genus = "Viola",
            imageURL = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Viola_tricolor_whole.jpg/1920px-Viola_tricolor_whole.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_27%29_%286972238046%29.jpg/1024px-Köhler%27s_Medizinal-Pflanzen_in_naturgetreuen_Abbildungen_mit_kurz_erläuterndem_Texte_%28Plate_27%29_%286972238046%29.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Viola_tricolor_garino_1.jpg/1920px-Viola_tricolor_garino_1.jpg"
            ),
            isLiked = false,
            veining = "Сетчатое",
            shape = "Сердцевидная")))
          /*  dao.upsertHerbs(listOf(
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
                            "На Руси эти растения именовались «роман», «романова трава», «романов цвет», «романник». Общепризнано, что эти названия восходят именно к прилагательному romanum (romana) в указанных латинских названиях, при этом одни авторы допускают возможность прямого заимствования слова из латыни западноевропейских средневековых травников и лечебников, другие же считают, что имело место заимствование из польского языка, выступившего в качестве языка-посредника[5]. Что касается слова «ромашка», то оно является уменьшительной формой от «роман» и впервые в литературе это слово было зафиксировано в 1778 году у А. Т. Болотова в его книге «Сельский житель»[5]. Лепестки лопатчатые",
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
                            "Листья супротивные, черешковые, яйцевидные, сетчатые до закруглённо-ромбических, городчато-пильчатые, опушённые.\n" +
                            "\n" +
                            "Цветки собраны в ложные кольца по 6—12; чашечка с нижними шиловидными зубцами, длинноволосистая и желёзистая; венчик синевато-белый или бледно-лиловый. Четыре тычинки, пестик с четырёхраздельной верхней завязью и длинным столбиком.\n" +
                            "\n" +
                            "Плод — крупный, состоит из четырёх орешков яйцевидной формы, чёрного цвета, блестящий. Масса 1000 семян — в среднем 0,62 г. Семена сохраняют всхожесть 2-3 года.\n" +
                            "\n" +
                            "Цветёт в июне—августе. Плоды созревают в августе—сентябре. Лепестки круглые\n" +
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
                            "Листья вегетативных побегов — стеблевые продолговатые супротивные, параллельные длиной 3,5—8 см, шириной 0,8—1,5 (до 4) см, туповатые или острые, при основании клиновидные или закруглённые, по краю мелко городчатые, морщинистые, нижние и средние на длинных черешках, верхние — сидячие. Прицветные — ланцетные, сидячие, в несколько раз меньше стеблевых. Жилкование сетчатое. Листья густоопушённые, серо-зелёные.\n" +
                            "\n" +
                            "Соцветия представлены колосовидным тирсом простые или ветвистые, с шестью-семью расставленными 10-цветковыми ложными мутовками; чашечка длиной 9—10 мм, почти до половины надрезанная на две губы; венчик фиолетовый, в два раза длиннее чашечки; столбик немного выставляется из венчика; рыльце с двумя неравными лопастями.\n" +
                            "\n" +
                            "Плод — орешек, диаметром 2,5 мм, почти округлый, тёмно-бурый, сухой, из четырёх долей.\n" +
                            "\n" +
                            "Биологические особенности\n" +
                            "В первый год жизни шалфей лекарственный образует к осени мощный кустарник.\n" +
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

            Русское название происходит от слова "чередовать", названо растение так из-за своего способа роста и распространения. Оно обладает способностью чередовать свои листья вдоль стебля, пальчатого, что создает характерный узор. Также, трава череда образует густые ковры на земле, поэтому ее ещё называют ковровой травой.
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
            Ranunculus aquatilis — многолетнее водное растение, образующее коврообразные покрытия на поверхности воды. Имеет ветвистые нитевидные подводные листья и зубчатые плавающие листья. В листьях крупные жилки расходятся в виде веера от основания листовой пластинки, а затем ветвятся, образуя сетку — это пальчатое (пальчато-сетчатое) жилкование.  . Листья — 2-3 см. В речных потоках с быстрым течением плавающие листья-поплавки могут и не образовываться. Цветки имеют лопатчатые белые лепестки с жёлтыми центрами и держатся на 1-2 см над водой. Плавающие листья являются опорами для цветков[2]. Плод-семянка 1,5-2 мм длиной с тонкими краями, сжатый с булавкообразным крючком длиной 0,6 мм[3]. Вид гексаплоидный с числом хромосом 2n = 48[4].

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
                    description = "Тысячелистник обыкновенный - многолетнее травянистое растение, известное своими лечебными свойствами. Растение из семейства Сложноцветные, в народе его называют «пахучая трава», «кровавник», «белоголовник», отличается сильным запахом. Он обладает противовоспалительным и ранозаживляющим действием. Используется в народной медицине для лечения ран, повреждений кожи, а также в качестве настоя при простудах и гриппе. Именно этот вид помогает поддерживать здоровье благодаря своему богатому химическому составу, включая эфирные масла и флавоноиды. Имеет сетчатое жилкование",
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
                    description = "Подснежники были известны с древнейших времён под разными названиями, но в 1753 году получили название Galanthus. По мере увеличения количества признанных видов предпринимались различные попытки разделить их на подгруппы, обычно на основе характера появления листьев (параллельного жилкования) (вегетации). В эпоху молекулярной филогенетики было показано, что эта характеристика ненадёжна, и теперь выделяют семь молекулярно определённых кладов, которые соответствуют биогеографическому распределению видов. Продолжают открываться новые виды.\n" +
                            "\n" +
                            "Большинство видов цветут зимой, до весеннего равноденствия (20 или 21 марта в Северном полушарии), но некоторые цветут ранней весной и поздней осенью.Для таких растений характерно дуговое или параллельное жилкование листьев. При этом среди однодольных изредка встречаются и растения с сетчатым жилкованием. Иногда подснежники путают с двумя родственными родами из трибы Galantheae, подснежниками Leucojum и Acis.",
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
                    description = "Фиалки — в большей части однолетние или многолетние травянистые растения, изредка полукустарники (виды, растущие в Андах), с попеременными, простыми или перисто-рассечёнными ланцетными листьями, снабжёнными прилистниками.\n" +
                            "\nЦветки одиночные, пазушные, обоеполые, зигоморфные (открытые и закрытые), лопатчатые околоцветник двойной: пять свободных остающихся чашелистиков с назад обращёнными придатками, пять свободных лепестков, из которых передний со шпорцем. Тычинок пять, они прижаты к пестику, нити у них короткие, передние две тычинки с мешковидным нектарником; связник расширяется над пыльниками в чешуйку. Пестик с верхней, одногнездой, многосемянной завязью, коротким столбиком и головчатым или пластинчатым рыльцем.\n" +
                            "\n" +
                            "Для листьев фиалки, например, фиалки трёхцветной, характерно сетчатое жилкование\n" +
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
                            "Лесная трава которую можно встретить и в парках" +
                            "\n" +
                            "Корневище короткое, не утолщённое, клубневидно-волокнистое.\n" +
                            "\n" +
                            "Листья формой напоминают печень, с чем связано название растения, собраны в прикорневую розетку, на более-менее длинных черешках, перистые, простые, мало-расчленённые, большей частью трёхлопастные, с цельнокрайными или крупно-зубчатыми лопастями.\n" +
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
                            "Цветки одиночные или в полузонтичных, часто многоцветковых соцветиях, небольшие или крупные, перистые. Околоцветники различной формы с 5—20 листочками и могут быть белого, пурпурного, синего, зелёного, жёлтого, розового или красного цветов. Цветки двуполые и радиально симметричные. Тычинки многочисленные, пестики многочисленные с одной висячей семяпочкой и одним покровом, опушённые или голые, с большей частью коротким, прямым или изогнутым столбиком, иногда без него. Листовые покрывала большей частью в количестве трёх приближены к цветоносам и сильно редуцированы, имеют большей частью вид чашелистиков.\n" +
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
                            "В России произрастает повсеместно, встречается в парках.\n" +
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
                            "Для сурепки (крестоцветных) характерно сетчатое жилкование листьев.\n" +
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
                            "Для листьев гусиного лука, как и для других представителей семейства Лилейных, характерно дуговое или линейное или параллельное жилкование\n" +
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
           )) */
        }
        var user = auth.currentUser
        if (user != null) {
            user!!.reload().await()
            user = auth.currentUser

            val herbs = dao.getHerbs()
                .map { it.copy(isLiked = it.likedAccountsUIDList.contains(user!!.uid)) }
            emit(herbs)
        }
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