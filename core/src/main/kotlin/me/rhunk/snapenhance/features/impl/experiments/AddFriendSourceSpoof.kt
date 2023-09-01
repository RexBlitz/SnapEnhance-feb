package me.rhunk.snapenhance.features.impl.experiments

import me.rhunk.snapenhance.features.Feature
import me.rhunk.snapenhance.features.FeatureLoadParams
import me.rhunk.snapenhance.hook.HookStage
import me.rhunk.snapenhance.hook.hook

class AddFriendSourceSpoof : Feature("AddFriendSourceSpoof", loadParams = FeatureLoadParams.ACTIVITY_CREATE_ASYNC) {
    override fun asyncOnActivityCreate() {
        val friendRelationshipChangerMapping = context.mappings.getMappedMap("FriendRelationshipChanger")

        findClass(friendRelationshipChangerMapping["class"].toString())
            .hook(friendRelationshipChangerMapping["addFriendMethod"].toString(), HookStage.BEFORE) { param ->
            val spoofedSource = context.config.experimental.addFriendSourceSpoof.getNullable() ?: return@hook

            context.log.verbose("addFriendMethod: ${param.args().toList()}", featureKey)

            fun setEnum(index: Int, value: String) {
                val enumData = param.arg<Any>(index)
                enumData::class.java.enumConstants.first { it.toString() == value }.let {
                    param.setArg(index, it)
                }
            }

            when (spoofedSource) {
                "added_by_group_chat" -> {
                    setEnum(1, "PROFILE")
                    setEnum(2, "GROUP_PROFILE")
                    setEnum(3, "ADDED_BY_GROUP_CHAT")
                }
                "added_by_username" -> {
                    setEnum(1, "SEARCH")
                    setEnum(2, "SEARCH")
                    setEnum(3, "ADDED_BY_USERNAME")
                }
                "added_by_qr_code" -> {
                    setEnum(1, "PROFILE")
                    setEnum(2, "PROFILE")
                    setEnum(3, "ADDED_BY_QR_CODE")
                }
                "added_by_mention" -> {
                    setEnum(1, "CONTEXT_CARDS")
                    setEnum(2, "CONTEXT_CARD")
                    setEnum(3, "ADDED_BY_MENTION")
                }
                "added_by_community" -> {
                    setEnum(1, "PROFILE")
                    setEnum(2, "PROFILE")
                    setEnum(3, "ADDED_BY_COMMUNITY")
                }
                else -> return@hook
            }
        }
    }
}