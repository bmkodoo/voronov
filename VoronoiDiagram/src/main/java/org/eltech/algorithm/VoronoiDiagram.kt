package org.eltech.algorithm

import org.eltech.structures.*
import org.eltech.utils.GeometryUtils
import java.util.*

class VoronoiDiagram {
    private var MIN_DIM: Double = 0.toDouble()
    private var MAX_DIM: Double = 0.toDouble()

    private var sweepLineY = java.lang.Double.POSITIVE_INFINITY
    private val arcComparator = ArcComparator(sweepLineY)
    private val events = PriorityQueue<Event>()
    private val arcs = TreeMap<Arc, Event?>(arcComparator)
    private val breakPoints = HashSet<BreakPoint>()
    private val edgeList = ArrayList<Edge>()

    fun initSweepLineValue(points: List<Point2D>) {
        var bottom = java.lang.Float.POSITIVE_INFINITY.toDouble()
        var top = java.lang.Float.NEGATIVE_INFINITY.toDouble()

        for (sight in points) {
            val y = sight.y
            if (y < bottom) bottom = y
            if (y > top) top = y
        }

        MAX_DIM = top + 1
        MIN_DIM = bottom - 1
        sweepLineY = MAX_DIM
        arcComparator.setSweepLineY(sweepLineY)
    }

    private fun build(sites: List<Point2D>): List<Edge> {
        initSweepLineValue(sites)
        for (site in sites) {
            events.add(Event(site))
        }

        while (!events.isEmpty()) {
            val cur = events.poll()
            sweepLineY = cur.sight.y
            arcComparator.setSweepLineY(sweepLineY)
            if (cur.type == Event.Type.SITE_EVENT) {
                handleSiteEvent(cur)
            } else {
                handleCircleEvent(cur)
            }
        }

        this.sweepLineY = MIN_DIM
        for (bp in breakPoints) {
            bp.finishEdge(sweepLineY)
        }
        return edgeList
    }

    private fun handleSiteEvent(cur: Event) {
        if (arcs.size == 0) {
            arcs.put(Arc(cur.sight), null)
            return
        }

        val arcEntryAbove = arcs.floorEntry(Arc(cur.sight, Arc.Type.QUERY))

        val falseEvent = arcEntryAbove.value
        if (falseEvent != null) {
            events.remove(falseEvent)
        }

        val arcAbove = arcEntryAbove.key
        val breakPointLeft = arcAbove.leftBreakPoint
        val breakPointRight = arcAbove.rightBreakPoint

        val newEdge = Edge(arcAbove.site, cur.sight)
        this.edgeList.add(newEdge)

        val newBreakPointLeft = BreakPoint(arcAbove.site,
                cur.sight,
                newEdge)
        val newBreakPointRight = BreakPoint(
                cur.sight,
                arcAbove.site,
                newEdge)
        breakPoints.add(newBreakPointLeft)
        breakPoints.add(newBreakPointRight)

        val leftArc = Arc(breakPointLeft, newBreakPointLeft)
        val centerArc = Arc(newBreakPointLeft, newBreakPointRight)
        val rightArc = Arc(newBreakPointRight, breakPointRight)

        arcs.remove(arcAbove)
        arcs.put(leftArc, null)
        arcs.put(centerArc, null)
        arcs.put(rightArc, null)

        checkForCircleEvent(leftArc)
        checkForCircleEvent(rightArc)
    }

    private fun handleCircleEvent(event: Event) {
        val rightEntry = arcs.higherEntry(event.arc)
        val leftEntry = arcs.lowerEntry(event.arc)


        if (rightEntry != null) {
            val falseEvent = rightEntry.value
            if (falseEvent != null) events.remove(falseEvent)
            arcs.put(rightEntry.key, null)
        }

        if (leftEntry != null) {
            val falseEvent = leftEntry.value
            if (falseEvent != null) events.remove(falseEvent)
            arcs.put(leftEntry.key, null)
        }

        arcs.remove(event.arc)

        event.arc.leftBreakPoint.finishEdge(event.center)
        event.arc.rightBreakPoint.finishEdge(event.center)

        breakPoints.remove(event.arc.leftBreakPoint)
        breakPoints.remove(event.arc.rightBreakPoint)

        val e = Edge(
                event.arc.leftBreakPoint.site1,
                event.arc.rightBreakPoint.site2)

        edgeList.add(e)

        if (leftEntry == null || rightEntry == null) {
            return
        }

        val leftArc = leftEntry.key
        val rightArc = rightEntry.key

        e.p1 = event.center

        val newBP = BreakPoint(
                event.arc.leftBreakPoint.site1,
                event.arc.rightBreakPoint.site2,
                e)
        breakPoints.add(newBP)

        rightArc.leftBreakPoint = newBP
        leftArc.rightBreakPoint = newBP

        checkForCircleEvent(leftArc)
        checkForCircleEvent(rightArc)
    }

    private fun checkForCircleEvent(arc: Arc) {
        val leftBreakPoint = arc.leftBreakPoint
        val rightBreakPoint = arc.rightBreakPoint

        if (leftBreakPoint == null || rightBreakPoint == null) {
            return
        }

        if (GeometryUtils.ccw(leftBreakPoint.site1, arc.site, rightBreakPoint.site2) != -1) {
            return
        }

        val circleCenter = leftBreakPoint.edge.intersection(rightBreakPoint.edge)

        if (circleCenter != null) {
            val radius = GeometryUtils.distanceTo(arc.site, circleCenter)
            val circleEventPoint = Point2D(circleCenter.x, circleCenter.y - radius)
            val ce = Event(arc, circleEventPoint, circleCenter)
            arcs.put(arc, ce)
            events.add(ce)
        }
    }

    companion object {

        fun createDiagram(points: List<Point2D>): List<Edge> {
            return VoronoiDiagram().build(points)
        }
    }
}

