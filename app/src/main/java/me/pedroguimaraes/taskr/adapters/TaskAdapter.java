package me.pedroguimaraes.taskr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.pedroguimaraes.taskr.R;
import me.pedroguimaraes.taskr.models.Task;

/**
 * Created by pedroguimaraes on 10/7/17.
 */



public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {


    private LayoutInflater mLayoutInflater;
    private Realm mRealm;
    private RealmResults<Task> mTasks;
    private RecyclerView mRecyclerView;


    public TaskAdapter(RealmQuery<Task> tasks) {
        mTasks = tasks.findAll().sort("id");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mLayoutInflater = LayoutInflater.from(parent.getContext());
        View view = mLayoutInflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Task task = mTasks.get(position);
        // Set item views based on your views and data model
        holder.mTaskTitle.setText(task.getTask_title());
        holder.mCreatedAt.setText(task.getCreated_at());
        holder.mCheckbox.setChecked(task.isDone());
        holder.mCheckbox.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.task_title)
        TextView mTaskTitle;
        @BindView(R.id.task_created_date)
        TextView mCreatedAt;
        @BindView(R.id.task_state_checkbox)
        CheckBox mCheckbox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnCheckedChanged(R.id.task_state_checkbox)
        void changeTaskStatus(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            mRealm = Realm.getDefaultInstance();
            mRealm.beginTransaction();
            if (isChecked) {
                mTasks.get(position).setDone(true);
            } else {
                mTasks.get(position).setDone(false);
            }
            mRealm.commitTransaction();
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    public RealmResults<Task> getmTasks() {
        return mTasks;
    }

    public Task getItem(int position) {
        return mTasks.get(position);
    }
}

