package si.gounitis.trimixmix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import si.gounitis.trimixmix.model.TrimixData;
import si.gounitis.trimixmix.util.Utils;

public class CalibrateFragment extends Fragment {

    private static final TrimixData trimixData = TrimixData.getTrimixData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_calibrate, container, false);

        attachCalibrateHandlers(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(getActivity(), "Clicked on " + item.getTitle(), Toast.LENGTH_SHORT)
                    .show();
        }
        return true;
    }

    private void attachCalibrateHandlers(View view) {
        final Button redCalibrateButton = view.findViewById(R.id.redCalibrateButton);
        redCalibrateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.calibrate(trimixData.getRedSensor());
            }
        });
        final Button greenCalibrateButton = view.findViewById(R.id.greenCalibrateButton);
        greenCalibrateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.calibrate(trimixData.getGreenSensor());
            }
        });
    }
}
