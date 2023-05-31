package br.com.heiderlopes.gamesgraphql1mobr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.heiderlopes.gamesgraphql1mobr.apollo.ApolloInstance
import br.com.heiderlopes.gamesgraphql1mobr.databinding.ActivityMainBinding
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainListAdapter.ClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var client: ApolloClient

    private var arrayList: ArrayList<Query.Game?>? = ArrayList()
    private lateinit var adapter: MainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        client = ApolloInstance().get()
        adapter = MainListAdapter(this)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.VERTICAL,
            false)
        binding.rvGames.layoutManager = mLayoutManager
        binding.rvGames.adapter = adapter

        binding.btAdd.setOnClickListener {
            val add = Intent(this, FormActivity::class.java)
            add.action = "add"
            activityResult.launch(add)
        }
        getGamesList()
    }

    private fun getGamesList() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val response = try {
                    client.query(Query()).execute()
                }catch (e : ApolloException){
                    binding.tvLoading.text = "Não foi possivel executar a acao"
                    binding.tvLoading.visibility = View.VISIBLE
                    return@repeatOnLifecycle
                }
                val games = response.data?.games
                if (games == null || response.hasErrors()) {
                    binding.tvLoading.text = response.errors?.get(0)?.message
                    binding.tvLoading.visibility = View.VISIBLE
                    return@repeatOnLifecycle
                }else {
                    arrayList = ArrayList(games)
                    adapter.setGames(arrayList)
                    if (arrayList?.isEmpty() == true) {
                        binding.tvLoading.visibility = View.VISIBLE
                        binding.tvLoading.text = "Nenhum dado encontrado"
                    } else {
                        binding.tvLoading.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onItemClickListener(position: Int) {
        val update = Intent(this, FormActivity::class.java)
        update.action = "update"
        update.putExtra("id", arrayList!![position]?.id.toString())
        activityResult.launch(update)
    }


    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getGamesList()
            }
        }

}
