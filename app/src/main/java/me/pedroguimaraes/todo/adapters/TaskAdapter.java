package me.pedroguimaraes.todo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.pedroguimaraes.todo.R;
import me.pedroguimaraes.todo.models.Task;

/**
 * Created by pedroguimaraes on 10/7/17.
 */



public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {


    private LayoutInflater mLayoutInflater;
    private RealmResults<Task> mTasks;


    public TaskAdapter(RealmQuery<Task> tasks) {
        mTasks = tasks.findAll().sort("id");
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
    }

    public RealmResults<Task> getmTasks() {
        return mTasks;
    }
}

