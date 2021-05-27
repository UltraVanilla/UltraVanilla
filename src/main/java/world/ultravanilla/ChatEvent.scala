package world.ultravanilla

import java.util.UUID

case class ChatEvent(
    channel: Integer,
    sender: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    message: String = "",
    senderNick: String = "",
    donator: Boolean = false,
    staff: Boolean = false,
    isDeath: Boolean = false,
    isAchievement: Boolean = false,
    source: ChatSource.Value
) {}
