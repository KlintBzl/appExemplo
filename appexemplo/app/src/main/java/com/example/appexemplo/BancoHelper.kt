package com.example.appexemplo
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoHelper(context: Context) :
    SQLiteOpenHelper(context, "escola.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE registros (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                descricao TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Neste exemplo, não precisamos atualizar a estrutura.
    }

    fun cadastrar(titulo: String, descricao: String): Long {
        val valores = ContentValues().apply {
            put("titulo", titulo)
            put("descricao", descricao)
        }

        return writableDatabase.insert(
            "registros",
            null,
            valores
        )
    }

    fun consultar(): List<String> {
        val lista = mutableListOf<String>()

        val cursor = readableDatabase.rawQuery(
            "SELECT id, titulo, descricao FROM registros ORDER BY id",
            null
        )

        try {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                val titulo = cursor.getString(1)
                val descricao = cursor.getString(2) ?: ""

                lista.add(
                    "ID: $id\nTítulo: $titulo\nDescrição: $descricao"
                )
            }
        } finally {
            cursor.close()
        }

        return lista
    }

    fun atualizar(
        id: Int,
        titulo: String,
        descricao: String
    ): Int {
        val valores = ContentValues().apply {
            put("titulo", titulo)
            put("descricao", descricao)
        }

        return writableDatabase.update(
            "registros",
            valores,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    fun excluir(id: Int): Int {
        return writableDatabase.delete(
            "registros",
            "id = ?",
            arrayOf(id.toString())
        )
    }

    fun contar(): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM registros",
            null
        )

        return try {
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        } finally {
            cursor.close()
        }
    }
}