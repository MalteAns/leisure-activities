package de.malteans.sosactivities.services

import de.malteans.sosactivities.CreateActivityReq
import de.malteans.sosactivities.models.Activity

interface ActivityService {
    fun list(from: String?, to: String?): List<Activity>
    fun get(id: String): Activity?
    fun create(req: CreateActivityReq): Activity
    fun patch(id: String, body: Map<String, Any?>): Activity?
    fun setImage(id: String, imageId: String?): Activity?
    fun delete(id: String): Int
}