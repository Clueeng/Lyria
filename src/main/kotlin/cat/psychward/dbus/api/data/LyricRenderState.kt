package cat.psychward.dbus.api.data

data class LyricRenderState(
    val current: LyricLine?,
    val surrounding: List<LyricLine>
)