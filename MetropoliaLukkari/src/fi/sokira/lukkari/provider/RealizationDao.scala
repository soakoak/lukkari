package fi.sokira.lukkari.provider

import fi.sokira.lukkari.provider.LukkariContract.Realization

object RealizationDao extends SQLiteQuery {

   override val tableName = Realization.PATH
}