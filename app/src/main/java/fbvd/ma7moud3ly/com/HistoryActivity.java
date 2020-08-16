package fbvd.ma7moud3ly.com;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ma7moud3ly.ustore.USon;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myRecyclerAdapter;
    private ArrayList<String> list = new ArrayList<>();
    private USon uson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        //read stored history json file
        uson = new USon(getApplicationContext(), "history");
        //init recycler view shows the history
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerAdapter = new MyRecyclerAdapter(list);
        recyclerView.setAdapter(myRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //red histoy to list and show all links...
        list.clear();
        list.addAll(uson.getList());
        myRecyclerAdapter.notifyDataSetChanged();
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
        private ArrayList<String> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView link;
            private ImageView del;

            public MyViewHolder(View view) {
                super(view);
                link = view.findViewById(R.id.link);
                del = view.findViewById(R.id.delete_item);

            }
        }

        public MyRecyclerAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item, parent, false);
            return new MyRecyclerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, int position) {
            final String link = list.get(position);
            holder.link.setText(link);
            holder.link.setOnClickListener((view) -> {
                openHistoryLink(position);
            });
            holder.del.setOnClickListener((view) -> {
                delHistoryLink(position);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    //when user click on list tab open open video
    private void openHistoryLink(int i) {
        Intent intent = new Intent(this, VideoActivity.class);
        String link = list.get(i);
        intent.putExtra("link", link);
        MainActivity.video_link = link;
        startActivity(intent);
    }

    //delete single link from history
    private void delHistoryLink(int i) {
        list.remove(i);
        uson.putList(list);
        myRecyclerAdapter.notifyDataSetChanged();
    }

    //clear all history
    public void clearHistory(View v) {
        list.clear();
        uson.putList(list);
        myRecyclerAdapter.notifyDataSetChanged();
    }
}