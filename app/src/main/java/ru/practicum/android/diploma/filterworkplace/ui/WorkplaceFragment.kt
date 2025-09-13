package ru.practicum.android.diploma.filterworkplace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding

class WorkplaceFragment : Fragment() {

    private var _binding: FragmentWorkplaceBinding? = null
    private val binding: FragmentWorkplaceBinding
        get() = _binding!!

    private val viewModel: WorkplaceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }
}


