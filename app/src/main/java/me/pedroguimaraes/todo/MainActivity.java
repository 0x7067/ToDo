package me.pedroguimaraes.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import me.pedroguimaraes.todo.adapters.TaskAdapter;
import me.pedroguimaraes.todo.models.Task;
import me.pedroguimaraes.todo.view.RecyclerViewEmptySupport;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_tasks)
    RecyclerViewEmptySupport mRecyclerView;

    @BindView(R.id.empty_view)
    TextView mTextView;

    private Realm mRealm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setEmptyView(mTextView);
        mRealm = Realm.getDefaultInstance();
        RealmQuery<Task> tasks = mRealm.where(Task.class);
        mRecyclerView.setAdapter(new TaskAdapter(this, tasks));
    }

    @OnClick(R.id.fab_add)
    public void onClickOpenNewTask(View v) {
        startActivity(new Intent(this, AddNewTaskActivity.class));
    }
    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
            mRealm = null;
        }
    }
}
