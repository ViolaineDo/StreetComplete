package de.westnordost.streetcomplete.quests.railway_crossing

import de.westnordost.osmapi.map.data.BoundingBox
import de.westnordost.osmapi.map.data.Element
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.osm.OsmElementQuestType
import de.westnordost.streetcomplete.data.osm.changes.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.download.MapDataWithGeometryHandler
import de.westnordost.streetcomplete.data.osm.download.OverpassMapDataDao
import de.westnordost.streetcomplete.data.osm.tql.OverpassQLUtil
import de.westnordost.streetcomplete.quests.AbstractQuestAnswerFragment

class AddRailwayCrossingBarrier(private val overpassMapDataDao: OverpassMapDataDao) : OsmElementQuestType<String> {
    override val commitMessage = "Add type of barrier for railway crossing"
    override val icon = R.drawable.ic_quest_railway

    override fun getTitle(tags: Map<String, String>) = R.string.quest_railway_crossing_barrier_title

    override fun createForm() = AddRailwayCrossingBarrierForm()

    override fun download(bbox: BoundingBox, handler: MapDataWithGeometryHandler): Boolean {
        return overpassMapDataDao.getAndHandleQuota(getOverpassQuery(bbox), handler);
    }

    override fun isApplicableTo(element: Element): Boolean? = null

    override fun applyAnswerTo(answer: String, changes: StringMapChangesBuilder) {
        changes.add("crossing:barrier", answer)
    }

    private fun getOverpassQuery(bbox: BoundingBox): String {
        val bboxFilter = OverpassQLUtil.getGlobalOverpassBBox(bbox)
        return bboxFilter + """
   way["highway"]["access"~"^private|no$"];
   node(w) -> .private;
   node["railway"="level_crossing"][!"crossing:barrier"]->.crossings;
   (.crossings; - .private;)->.public;
   .public out;"""
    }
}
