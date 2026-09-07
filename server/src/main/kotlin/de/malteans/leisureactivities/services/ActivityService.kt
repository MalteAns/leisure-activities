package de.malteans.leisureactivities.services

import de.malteans.leisureactivities.dto.CreateActivityReq
import de.malteans.leisureactivities.dto.UpdateActivityReq
import de.malteans.leisureactivities.model.Activity

interface ActivityService {
    fun list(from: String?, to: String?): List<Activity>
    fun get(id: String): Activity?
    fun create(req: CreateActivityReq): Activity
    fun update(id: String, updateActivityReq: UpdateActivityReq): Activity?
    fun delete(id: String): Int
}