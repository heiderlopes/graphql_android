package br.com.heiderlopes.gamesgraphql1mobr

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.heiderlopes.gamesgraphql1mobr.databinding.GameItemBinding
import com.squareup.picasso.Picasso


class MainListAdapter(listener: ClickListener) : RecyclerView.Adapter<MainListAdapter.GamesHolder>() {

    private lateinit var context: Context
    private var selectedItemPosition: Int
    private var games: ArrayList<Query.Game?>
    private val listener: ClickListener

    init {
        selectedItemPosition = -1
        this.games = ArrayList()
        this.listener = listener
    }

    fun setGames(games: ArrayList<Query.Game?>?){
        this.games = ArrayList(games)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesHolder {
        val binding = GameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        this.context = parent.context
        return GamesHolder(binding)
    }

    override fun onBindViewHolder(holder: GamesHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.adapterPosition
            notifyDataSetChanged()
            listener.onItemClickListener(position)
        }
    }

    override fun getItemCount() = games.size

    inner class GamesHolder(private val binding: GameItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            val game = games[position]
            binding.tvName.text = game?.name
            binding.tvType.text = game?.type?.type?.rawValue
            Picasso.get()
                .load(game?.imageURL)
                .error(R.mipmap.ic_launcher)  // Imagem caso nao consiga recuperar
                .placeholder(R.mipmap.ic_launcher) // Imagem enquanto esta buscando a imagem solicitada
                .into(binding.ivGame)
        }
    }

    interface ClickListener {
        fun onItemClickListener(position: Int)
    }

}
