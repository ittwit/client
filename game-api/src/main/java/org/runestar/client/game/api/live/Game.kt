package org.runestar.client.game.api.live

import hu.akarnokd.rxjava2.swing.SwingObservable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.kxtra.swing.component.window
import org.runestar.client.game.api.*
import org.runestar.client.game.raw.CLIENT
import org.runestar.client.game.raw.access.XClient
import org.runestar.client.game.raw.access.XPacketBuffer
import java.awt.Component
import java.awt.Container

object Game {

    val state get() = GameState.of(CLIENT.gameState)

    val stateChanges: Observable<GameState> = XClient.updateGameState.exit.map {
        checkNotNull(GameState.of(it.arguments[0] as Int)) { it.arguments[0] }
    }

    val ticks: Observable<Unit> = XPacketBuffer.readSmartByteShortIsaac.exit
            .filter { it.returned == 33 } // update npcs
            .map { Unit }
            .delay { XClient.doCycle.enter }

    val cycle get() = CLIENT.cycle

    val plane get() = CLIENT.plane

    val runEnergy get() = CLIENT.runEnergy

    val weight get() = CLIENT.weight

    val windowMode get() = WindowMode.of(CLIENT.clientPreferences.windowMode)

    /**
     * @see[java.awt.event.WindowListener]
     * @see[java.awt.event.WindowStateListener]
     * @see[java.awt.event.WindowFocusListener]
     */
    val windowEvents = SwingObservable.window(
            checkNotNull((CLIENT as Component).window) { "Client has no window" }
    )

    /**
     * @see[java.awt.event.ContainerListener]
     */
    val containerEvents = SwingObservable.container(CLIENT as Container)

    fun getVarbit(varbitId: Int): Int = CLIENT.getVarbit(varbitId)

    val varps: IntArray = CLIENT.varps_main

    val varcs: Varcs = Varcs(CLIENT.varcs)

    val clanChat: ClanChat? get() = CLIENT.clanChat?.let { ClanChat(it) }

    val friendsSystem: FriendsSystem get() = FriendsSystem(CLIENT.friendSystem)

    val specialAttackEnabled get() = varps[VarpId.SPECIAL_ATTACK_ENABLED] != 0

    /**
     * 0 - 100
     */
    val specialAttackPercent get() = varps[VarpId.SPECIAL_ATTACK_PERCENT] / 10

    val visibilityMap = VisibilityMap(CLIENT.visibilityMap, LiveCamera)

    val destination: SceneTile? get() {
        val x = CLIENT.destinationX
        val y = CLIENT.destinationY
        if (x == 0 && y == 0) return null
        return SceneTile(x, y, plane)
    }

    val executor = ObservableExecutor(XClient.doCycle.exit)

    val scheduler = Schedulers.from(executor)

    val hintArrow: HintArrow? get() = when (CLIENT.hintArrowType) {
        1 -> Npcs[CLIENT.hintArrowNpcIndex]?.let { HintArrow.OnNpc(it) }
        2 -> HintArrow.Static(
                Position(
                        Position.toLocal(CLIENT.hintArrowX - CLIENT.baseX, CLIENT.hintArrowSubX),
                        Position.toLocal(CLIENT.hintArrowY - CLIENT.baseY, CLIENT.hintArrowSubY),
                        CLIENT.hintArrowHeight * 2,
                        CLIENT.plane
                )
        )
        10 -> Players[CLIENT.hintArrowPlayerIndex]?.let { HintArrow.OnPlayer(it) }
        else -> null
    }
}