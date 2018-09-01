package np.com.aanalbasaula.foodplanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private static class TestAsync extends AsyncTask<Void, Void, Void> {
        private RecipeDao dao;

        TestAsync(RecipeDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //TODO test recipe creation; ingredient creation and then relationship creation
            return null;
        }
    }
}
