package org.runestar.client.game.api.live

import org.runestar.client.game.api.GraphicsObject
import org.runestar.client.game.api.NodeDeque
import org.runestar.client.game.raw.CLIENT
import org.runestar.client.game.raw.access.XGraphicsObject

object GraphicsObjects : NodeDeque<GraphicsObject, XGraphicsObject>(CLIENT.graphicsObjects) {

    override fun wrap(n: XGraphicsObject): GraphicsObject = GraphicsObject(n)
}