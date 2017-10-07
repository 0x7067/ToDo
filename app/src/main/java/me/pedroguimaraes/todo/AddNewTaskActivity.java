package me.pedroguimaraes.todo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import me.pedroguimaraes.todo.models.Task;

public class AddNewTaskActivity extends AppCompatActivity implements RealmChangeListener<Realm> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - h:mm a");

    @BindView(R.id.editText)
    EditText mEditTextTitle;

    private Realm mRealm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.imageButton)
    public void onClickSave(View v) {

        if(mEditTextTitle.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a task title", Toast.LENGTH_SHORT).show();
        } else {

            mRealm.beginTransaction();

            Task task = mRealm.createObject(Task.class, UUID.randomUUID().toString());
            task.setTask_title(mEditTextTitle.getText().toString());
            task.setCreated_at(dateFormat.format(new Date()));
            task.setDone(false);

            mRealm.commitTransaction();
            mRealm.addChangeListener(this);
            onBackPressed();
        }
    }

    @Override
    public void onChange(Realm element) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.removeChangeListener(this);
        mRealm.close();
    }
}

