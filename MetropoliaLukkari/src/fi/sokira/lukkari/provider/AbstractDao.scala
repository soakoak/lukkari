package fi.sokira.lukkari.provider

abstract class AbstractDao extends BaseDao
      with SQLiteQuery with SQLiteInsert with SQLiteUpdate with SQLiteDelete {

}