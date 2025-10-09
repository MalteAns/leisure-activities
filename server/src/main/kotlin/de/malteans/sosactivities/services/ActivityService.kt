package de.malteans.sosactivities.services

import de.malteans.sosactivities.dto.CreateActivityReq
import de.malteans.sosactivities.dto.UpdateActivityReq
import de.malteans.sosactivities.model.Activity

interface ActivityService {
    fun list(from: String?, to: String?): List<Activity>
    fun get(id: String): Activity?
    fun create(req: CreateActivityReq): Activity
    fun update(id: String, updateActivityReq: UpdateActivityReq): Activity?
    fun delete(id: String): Int
}