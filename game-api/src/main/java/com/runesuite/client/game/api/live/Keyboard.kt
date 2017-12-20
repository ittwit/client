package com.runesuite.client.game.api.live

import com.runesuite.client.game.raw.Client
import com.runesuite.client.game.raw.access.XGameShell
import hu.akarnokd.rxjava2.swing.SwingObservable
import io.reactivex.Observable
import java.awt.event.KeyEvent

object Keyboard {

    /**
     * @see[java.awt.event.KeyListener]
     */
    val events: Observable<KeyEvent> = LiveCanvas.canvasReplacements.flatMap { SwingObservable.keyboard(it) }
}