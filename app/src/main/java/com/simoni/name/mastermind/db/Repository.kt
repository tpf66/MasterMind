package com.simoni.name.mastermind.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Repository(private val dao: DaoGame) {

    fun readAll(): Game? {
        return dao.selectAll()
    }

    fun insert(game: Game){
        CoroutineScope(Dispatchers.IO).launch{ dao.insert(game) }
    }

    fun delete(game: Game){
        CoroutineScope(Dispatchers.IO).launch{ dao.delete(game) }
    }
}