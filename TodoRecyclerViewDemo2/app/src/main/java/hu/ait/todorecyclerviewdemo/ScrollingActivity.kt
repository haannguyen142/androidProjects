package hu.ait.todorecyclerviewdemo

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import hu.ait.todorecyclerviewdemo.adapter.TodoAdapter
import hu.ait.todorecyclerviewdemo.data.AppDatabase
import hu.ait.todorecyclerviewdemo.data.Todo
import hu.ait.todorecyclerviewdemo.databinding.ActivityScrollingBinding
import hu.ait.todorecyclerviewdemo.dialog.TodoDialog
import hu.ait.todorecyclerviewdemo.touch.TodoReyclerTouchCallback
import kotlin.concurrent.thread

class ScrollingActivity : AppCompatActivity(), TodoDialog.TodoHandler {

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
//            adapter.addTodo(Todo("2022 . 03. 21",false,"Demo"))

            TodoDialog().show(supportFragmentManager,"TODO_DIALOG")
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        thread {
            val todoItems = AppDatabase.getInstance(this).todoDao().getAllTodos()

            runOnUiThread {
                adapter = TodoAdapter(this, todoItems)
                binding.recyclerTodo.adapter = adapter

                val touchCallbakList = TodoReyclerTouchCallback(adapter)
                val itemTouchHelper = ItemTouchHelper(touchCallbakList)
                itemTouchHelper.attachToRecyclerView(binding.recyclerTodo)
            }
        }

    }

    override fun todoCreated(todo: Todo) {
        thread {
            AppDatabase.getInstance(this).todoDao().insertTodo(todo)
            runOnUiThread {
                adapter.addTodo(todo)

                Snackbar.make(binding.root, "Todo created",Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        adapter.deleteLastItem()
                    }
                    .show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}