package br.com.heiderlopes.gamesgraphql1mobr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.heiderlopes.gamesgraphql1mobr.apollo.ApolloInstance
import br.com.heiderlopes.gamesgraphql1mobr.databinding.ActivityFormBinding
import br.com.heiderlopes.gamesgraphql1mobr.type.GameType
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding

    private lateinit var client: ApolloClient

    private lateinit var activityAction: String
    private lateinit var id: String

    private var game: GameByIdQuery.Game? =
        GameByIdQuery.Game("", "", "", GameByIdQuery.Type("", GameType.FISICO))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = ApolloInstance().get()

        binding.btSave.setOnClickListener {
            when (activityAction) {
                "add" -> insertGame()
                "update" -> updateGame()
            }
        }

        binding.btDelete.setOnClickListener {
            val deleteDialog = AlertDialog.Builder(this)
            deleteDialog.setTitle(R.string.app_name)
            deleteDialog.setMessage("deletegame?")
            deleteDialog.setNegativeButton("NO") { _, _ ->
                deleteDialog.setCancelable(true)
            }
            deleteDialog.setPositiveButton("YES") { _, _ ->
                deleteGame()
            }
            deleteDialog.show()
        }

        val data = intent
        activityAction = data.action!!
        if (activityAction == "update") {
            id = data.getStringExtra("id")!!
            getGameById()
        }

    }

    private fun getGameById() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val result = try {
                    client.query(GameByIdQuery(id)).execute()
                } catch (e: ApolloException) {
                    Toast.makeText(
                        this@FormActivity,
                        "Erro ao pesquisar os dados",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return@repeatOnLifecycle
                }

                game = result.data?.game

                if (result.hasErrors()) {
                    val message = result.errors?.get(0)?.message
                    Toast.makeText(this@FormActivity, message, Toast.LENGTH_LONG).show()
                    return@repeatOnLifecycle
                } else {

                    binding.etName.setText(game?.name)
                    binding.etImageURL.setText(game?.imageURL)
                    binding.etType.setText(game?.type?.id)

                    Picasso.get()
                        .load(game?.imageURL)
                        .into(binding.ivGame)

                    binding.btDelete.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun insertGame() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val result = try {
                    client.mutation(
                        InsertGameMutation(
                            binding.etName.text.toString(),
                            true,
                            binding.etImageURL.text.toString(),
                            GameType.FISICO
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Toast.makeText(
                        this@FormActivity,
                        "Não foi possivel realizar a operacao",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return@repeatOnLifecycle
                }
                if (result.hasErrors()) {
                    val message = result.errors?.get(0)?.message
                    Toast.makeText(this@FormActivity, message, Toast.LENGTH_LONG).show()
                    return@repeatOnLifecycle
                } else {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun deleteGame() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val result = try {
                    client.mutation(DeleteGameMutation(id)).execute()
                } catch (e: ApolloException) {
                    Toast.makeText(
                        this@FormActivity,
                        "Não foi possivel realizar a operacao",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return@repeatOnLifecycle
                }

                if (result.hasErrors()) {
                    val message = result.errors?.get(0)?.message
                    Toast.makeText(this@FormActivity, message, Toast.LENGTH_LONG).show()
                    return@repeatOnLifecycle
                } else {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun updateGame() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val result = try {
                    client.mutation(
                        UpdateGameMutation(
                            id = id,
                            binding.etName.text.toString(),
                            true,
                            binding.etImageURL.text.toString(),
                            GameType.FISICO
                        )
                    ).execute()
                } catch (e: ApolloException) {
                    Toast.makeText(
                        this@FormActivity,
                        "Não foi possivel executar a operacao",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return@repeatOnLifecycle
                }
                if (result.hasErrors()) {
                    val message = result.errors?.get(0)?.message
                    Toast.makeText(this@FormActivity, message, Toast.LENGTH_LONG).show()
                    return@repeatOnLifecycle
                } else {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }
}
