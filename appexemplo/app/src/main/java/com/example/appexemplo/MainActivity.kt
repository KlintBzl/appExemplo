package com.example.appexemplo


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var banco: BancoHelper

    private lateinit var edtId: EditText
    private lateinit var edtTitulo: EditText
    private lateinit var edtDescricao: EditText
    private lateinit var txtTotal: TextView
    private lateinit var txtSql: TextView
    private lateinit var txtResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        banco = BancoHelper(this)

        edtId = findViewById(R.id.edtId)
        edtTitulo = findViewById(R.id.edtTitulo)
        edtDescricao = findViewById(R.id.edtDescricao)
        txtTotal = findViewById(R.id.txtTotal)
        txtSql = findViewById(R.id.txtSql)
        txtResultado = findViewById(R.id.txtResultado)

        findViewById<Button>(R.id.btnCadastrar)
            .setOnClickListener {
                cadastrar()
            }

        findViewById<Button>(R.id.btnConsultar)
            .setOnClickListener {
                consultar()
            }

        findViewById<Button>(R.id.btnAtualizar)
            .setOnClickListener {
                atualizar()
            }

        findViewById<Button>(R.id.btnExcluir)
            .setOnClickListener {
                excluir()
            }

        findViewById<Button>(R.id.btnLimpar)
            .setOnClickListener {
                limparCampos()
            }

        atualizarTotal()
        consultar()
    }

    private fun cadastrar() {
        val titulo = edtTitulo.text.toString().trim()
        val descricao = edtDescricao.text.toString().trim()

        if (titulo.isEmpty()) {
            edtTitulo.error = "Informe um título"
            edtTitulo.requestFocus()
            return
        }

        val id = banco.cadastrar(titulo, descricao)

        if (id != -1L) {
            mostrarSql(
                "INSERT INTO registros (titulo, descricao)\n" +
                        "VALUES (?, ?);",
                "INSERT adiciona um novo registro à tabela."
            )

            mostrarMensagem("Registro cadastrado. ID: $id")
            limparCampos()

        } else {
            mostrarMensagem("Não foi possível cadastrar.")
        }
    }

    private fun consultar() {
        val registros = banco.consultar()

        mostrarSql(
            "SELECT id, titulo, descricao\n" +
                    "FROM registros ORDER BY id;",
            "SELECT consulta os registros armazenados."
        )

        txtResultado.text = if (registros.isEmpty()) {
            "Nenhum registro cadastrado."
        } else {
            registros.joinToString("\n\n──────────────\n\n")
        }

        atualizarTotal()
    }

    private fun atualizar() {
        val id = edtId.text.toString().toIntOrNull()
        val titulo = edtTitulo.text.toString().trim()
        val descricao = edtDescricao.text.toString().trim()

        if (id == null || id <= 0) {
            edtId.error = "Informe um ID válido"
            return
        }

        if (titulo.isEmpty()) {
            edtTitulo.error = "Informe um título"
            return
        }

        val quantidade = banco.atualizar(id, titulo, descricao)

        mostrarSql(
            "UPDATE registros\n" +
                    "SET titulo = ?, descricao = ?\n" +
                    "WHERE id = ?;",
            "UPDATE modifica os dados do registro " +
                    "identificado pelo ID."
        )

        if (quantidade > 0) {
            mostrarMensagem("Registro atualizado.")
        } else {
            mostrarMensagem("Nenhum registro encontrado com esse ID.")
        }

        consultar()
    }

    private fun excluir() {
        val id = edtId.text.toString().toIntOrNull()

        if (id == null || id <= 0) {
            edtId.error = "Informe o ID para excluir"
            return
        }

        val quantidade = banco.excluir(id)

        mostrarSql(
            "DELETE FROM registros WHERE id = ?;",
            "DELETE remove o registro identificado pelo ID."
        )

        if (quantidade > 0) {
            mostrarMensagem("Registro excluído.")
            limparCampos()
        } else {
            mostrarMensagem("Nenhum registro encontrado com esse ID.")
        }


    }

    private fun atualizarTotal() {
        txtTotal.text = "Total de registros: ${banco.contar()}"
    }

    private fun mostrarSql(comando: String, explicacao: String) {
        txtSql.text = "$comando\n\nExplicação: $explicacao"
    }

    private fun mostrarMensagem(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }

    private fun limparCampos() {
        edtId.text.clear()
        edtTitulo.text.clear()
        edtDescricao.text.clear()
        edtTitulo.requestFocus()
    }

    override fun onDestroy() {
        banco.close()
        super.onDestroy()
    }
}
