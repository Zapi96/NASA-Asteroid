package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidsApiFilter
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModelFactory(activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        var error = false

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AsteroidGridAdapter(AsteroidGridAdapter.OnClickListener{
            viewModel.displayAsteroidDetails(it)
        })

        binding.asteroidRecycler.adapter = adapter

        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        /**
         * Submit list item to recyclerview to display
         * */
        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.picture.observe(viewLifecycleOwner, Observer {

            if (it!=null && it.mediaType == "image" ){
                Picasso.get().load(it.url).into(binding.activityMainImageOfTheDay)
                binding.textView.text = it.title
                binding.activityMainImageOfTheDay.contentDescription = it.title
            }
            else {
                error = true
            }
        })

        viewModel.pictureExtra.observe(viewLifecycleOwner, Observer {
            if (error && it!=null && it.mediaType == "image") {
                Picasso.get().load(it.url).into(binding.activityMainImageOfTheDay)
                binding.textView.text = it.title
                binding.activityMainImageOfTheDay.contentDescription = it.title
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_today_menu -> AsteroidsApiFilter.SHOW_TODAY
                R.id.show_saved_menu -> AsteroidsApiFilter.SHOW_SAVED
                else -> AsteroidsApiFilter.SHOW_WEEK
            }
        )
        return true
    }




}
