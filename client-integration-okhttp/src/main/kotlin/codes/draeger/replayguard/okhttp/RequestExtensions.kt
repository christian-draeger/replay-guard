package codes.draeger.replayguard.okhttp

import okhttp3.Request

object ReplayGuardEnabled

fun Request.Builder.enableReplayGuard(): Request.Builder {
    this.tag(ReplayGuardEnabled::class.java, ReplayGuardEnabled)
    return this
}

fun Request.Builder.disableReplayGuard(): Request.Builder {
    this.tag(ReplayGuardEnabled::class.java, null)
    return this
}
