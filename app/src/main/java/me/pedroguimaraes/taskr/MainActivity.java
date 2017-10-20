package me.pedroguimaraes.taskr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.pedroguimaraes.taskr.adapters.TaskAdapter;
import me.pedroguimaraes.taskr.models.Task;
import me.pedroguimaraes.taskr.utils.ItemClickSupport;
import me.pedroguimaraes.taskr.view.RecyclerViewEmptySupport;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_tasks)
    RecyclerViewEmptySupport mRecyclerView;

    @BindView(R.id.empty_view_text)
    TextView mTextView;
    @BindView(R.id.empty_view_image)
    ImageView mImageView;
    @BindView(R.id.empty_view)
    LinearLayout mLinearLayout;

    private Realm mRealm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setEmptyView(mLinearLayout);
        mRealm = Realm.getDefaultInstance();
        final RealmQuery<Task> tasksQuery = mRealm.where(Task.class);
        mRecyclerView.setAdapter(new TaskAdapter(tasksQuery));

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // TODO: Edit activity aqui
            }
        });

        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                deleteTask(position, tasksQuery);
                mRecyclerView.getAdapter().notifyDataSetChanged();
                return true;
            }
        });
    }

    @OnClick(R.id.fab_add)
    public void onClickOpenNewTask(View v) {
        startActivity(new Intent(this, AddNewTaskActivity.class));
    }


    // Delete assincrono que eu não consegui fazer funcionar (grrr)

//    public void deleteTask(final int position) {
//        mRealm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm mRealm) {
//                mRealm = Realm.getDefaultInstance();
//                RealmResults<Task> toDeleteTask = mRealm.where(Task.class).equalTo("id", position).findAll();
//                if(toDeleteTask != null) {
//                    toDeleteTask.deleteAllFromRealm();
//                    mRecyclerView.getAdapter().notifyDataSetChanged();
//                }
//            }
//        }, new Realm.Transaction.OnSuccess() {
//            @Override
//            public void onSuccess() {
//                // Transaction was a success.
//                Log.v("database", "Delete ok");
//            }
//        }, new Realm.Transaction.OnError() {
//            @Override
//            public void onError(Throwable error) {
//                // Transaction failed and was automatically canceled.
//                Log.e("database", error.getMessage());
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteCompletedTasks();
                mRecyclerView.getAdapter().notifyDataSetChanged();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Settings não implementado", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_github:
                i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/0x7067/ToDo"));
                startActivity(i);
                break;
            case R.id.action_feedback:
                i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/0x7067/ToDo/issues"));
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(final int position, RealmQuery<Task> tasksQuery) {
        final RealmResults<Task> tasks = tasksQuery.findAll().sort("id");
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Task> toDeleteTask = realm.where(Task.class).equalTo("id", tasks.get(position).getId()).findAll();
                    Log.d("TAG", toDeleteTask.toString());
                    toDeleteTask.deleteAllFromRealm();
                }
            });
    } finally {
            realm.close();
        }
    }

    private void deleteCompletedTasks() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Task> toDeleteTask = realm.where(Task.class).equalTo("done", true).findAll();
                    Log.d("TAG", toDeleteTask.toString());
                    toDeleteTask.deleteAllFromRealm();
                }
            });
        } finally {
            realm.close();
        }

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
