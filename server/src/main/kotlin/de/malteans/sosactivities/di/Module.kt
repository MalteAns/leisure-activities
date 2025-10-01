package de.malteans.sosactivities.di

import de.malteans.sosactivities.security.token.JwtTokenService
import de.malteans.sosactivities.security.token.TokenService
import de.malteans.sosactivities.services.*
import de.malteans.sosactivities.services.impl.*
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val module = module {
    single<TokenService> { JwtTokenService() }

    // Database and related services
    single<DatabaseService> { DatabaseServiceImpl(get()) }
    single { get<DatabaseService>().createDatabase() }

    single<Database> {
        Database.connect(
            url = "jdbc:mariadb://192.168.178.129:3306/sosActivities",
            driver = "org.mariadb.jdbc.Driver",
            user = "root",
            password = "NPf4IfvrHUBElPllKdrZ",
        )
    }

    // DB implemented services
    single<UserService> { UserServiceImpl(get()) }
    single<ActivityService> { ActivityServiceImpl(get()) }
    single<RegistrationService> { RegistrationServiceImpl(get()) }
    single<ImageService> { ImageServiceImpl(get()) }

    // In Memory implementations for dev/testing
//    single<UserService> { InMemUserService() }
//    single<RegistrationService> { InMemRegistrationService(get()) }
//    single<ActivityService> { InMemActivityService(get()) }
//    single<ImageService> { InMemImageService() }
}

/* ------------ In-memory implementations (dev only) ------------- */
/*
private class InMemUserService : UserService {
    private val users = ConcurrentHashMap<String, User>()
    override fun insert(id: String, firstName: String, lastName: String): User {
        val u = users[id]?.copy(firstName = firstName, lastName = lastName)
            ?: User(id, firstName, lastName)
        users[id] = u
        return u
    }
    override fun get(id: String) = users[id] ?: User(id, "Anonymous", "User")
    override fun updateName(id: String, firstName: String, lastName: String): User {
        val u = users[id]?.copy(firstName = firstName, lastName = lastName)
            ?: User(id, firstName, lastName)
        users[id] = u
        return u
    }
    override fun updateIsStaff(id: String, value: Boolean): User {
        val u = users[id]?.copy(isStaff = value)
            ?: User(id, "Anonymous", "User", isStaff = value)
        users[id] = u
        return u
    }
}

private class InMemActivityService(
    private val registrationService: RegistrationService,
) : ActivityService {
    private val items = ConcurrentHashMap<String, Activity>()
    override fun list(from: String?, to: String?): List<Activity> = items.values.sortedBy { it.startsAt }
    override fun get(id: String) = items[id]
    override fun create(req: CreateActivityReq): Activity {
        val id = UUID.randomUUID().toString()
        val a = Activity(
            id = id,
            title = req.title,
            description = req.description,
            startsAt = Instant.parse(req.startsAt),
            durationMin = req.durationMin,
            imageId = req.imageId
        )
        items[id] = a
        return a
    }
    override fun patch(id: String, body: Map<String, Any?>): Activity? {
        val cur = items[id] ?: return null
        val upd = cur.copy(
            title = (body["title"] as? String) ?: cur.title,
            description = (body["description"] as? String?) ?: cur.description,
            startsAt = (body["startsAt"] as? String)?.let(Instant::parse) ?: cur.startsAt,
            durationMin = (body["durationMin"] as? Number)?.toInt() ?: cur.durationMin,
            imageId = (body["imageId"] as? String?) ?: cur.imageId
        )
        items[id] = upd; return upd
    }
    override fun setImage(id: String, imageId: String?): Activity? = patch(id, mapOf("imageId" to imageId))
    override fun delete(id: String): Int {
        return items.remove(id)
            .let { if (it != null) 1 else 0 }
    }
}

private class InMemRegistrationService(
    private val users: UserService,
) : RegistrationService {
    // activityId -> (userId -> status)
    private val regs = ConcurrentHashMap<String, MutableMap<String, String>>()

    override fun register(activityId: String, userId: String): String {
        val map = regs.computeIfAbsent(activityId) { ConcurrentHashMap() }
        val status = map.getOrPut(userId) { "CONFIRMED" }
        return status
    }

    override fun cancel(activityId: String, userId: String): Int {
        return regs[activityId]?.remove(userId)
            .let { if (it != null) 1 else 0 }
    }

    override fun roster(activityId: String): Roster {
        val map = regs[activityId].orEmpty()
        val confirmed = map.filterValues { it == "CONFIRMED" }.keys.map { id ->
            val u = users.get(id); RegItem(u.id, u.displayName)
        }
        val waitlist = map.filterValues { it == "WAITLISTED" }.keys.map { id ->
            val u = users.get(id); RegItem(u.id, u.displayName)
        }
        return Roster(confirmed, waitlist)
    }
}

private class InMemImageService : ImageService {
    private val store = ConcurrentHashMap<String, Map<String, Any?>>()
    override fun presignUpload(req: PresignReq): Presign {
        val id = UUID.randomUUID().toString()
        val key = "uploads/$id/${req.filename}"
        val putUrl = "https://example.invalid/$key?signature=fake" // replace with real presign later
        store[id] = mapOf("id" to id, "key" to key, "url" to putUrl)
        return Presign(id, putUrl, key)
    }
    override fun finalize(id: String): ImageMeta {
        val key = store[id]?.get("key") as? String ?: "uploads/$id/file"
        val public = "https://cdn.invalid/$key"
        return ImageMeta(id, public, null, null)
    }
    override fun list(mine: Boolean): List<Map<String, Any?>> = store.values.toList()
    override fun delete(id: String): Int {
        return store.remove(id)
            .let { if (it != null) 1 else 0 }
    }
}
*/