package me.pedroguimaraes.taskr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import me.pedroguimaraes.taskr.models.Task;

public class AddNewTaskActivity extends AppCompatActivity implements RealmChangeListener<Realm> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - h:mm a");
    private MenuItem doneMenuItem;
    @BindView(R.id.editText)
    EditText mEditTextTitle;

    private Realm mRealm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.task_create);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDoneMenuItem();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_create, menu);
        doneMenuItem = menu.findItem(R.id.action_done);
        updateDoneMenuItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                mRealm.beginTransaction();
                Task task = mRealm.createObject(Task.class, System.currentTimeMillis());
                task.setTask_title(mEditTextTitle.getText().toString());
                task.setCreated_at(dateFormat.format(new Date()));
                task.setDone(false);
                mRealm.addChangeListener(this);
                mRealm.commitTransaction();
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    private void updateDoneMenuItem() {
        if (enableToSave()) {
            doneMenuItem.setEnabled(true);
            doneMenuItem.getIcon().setAlpha(255);
        } else {
            doneMenuItem.setEnabled(false);
            doneMenuItem.getIcon().setAlpha(127);
        }
    }

    private boolean enableToSave() {
        return !(mEditTextTitle.getText().toString().matches(""));

    }
}
