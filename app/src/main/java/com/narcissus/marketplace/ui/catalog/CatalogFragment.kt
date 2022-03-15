package com.narcissus.marketplace.ui.catalog

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.narcissus.marketplace.R
import com.narcissus.marketplace.databinding.FragmentCatalogBinding

class CatalogFragment : Fragment(R.layout.fragment_catalog) {
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatalogViewModel by viewModels()

    private val catalogAdapter by lazy {
        ListDelegationAdapter(
            DepartmentListItem.delegate(viewLifecycleOwner.lifecycleScope),
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCatalogBinding.bind(view)
        initDepartmentsRecyclerView()
        initSearchViewListener()
        subscribeToViewModel()
    }

    private fun initDepartmentsRecyclerView() {
        binding.rvDepartment.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDepartment.adapter = catalogAdapter
    }

    private fun initSearchViewListener() {
        val sv = binding.searchLayout.findViewById<SearchView>(R.id.searchView)
        sv.setOnQueryTextFocusChangeListener { view, b ->
            sv.setOnQueryTextFocusChangeListener(null)
            navigateToSearch()
        }
    }

    private fun subscribeToViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.departments.collect(catalogAdapter::setItems)
        }
    }

    private fun navigateToSearch() {
        findNavController().navigate(
            CatalogFragmentDirections.actionFragmentCatalogToSearch()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
