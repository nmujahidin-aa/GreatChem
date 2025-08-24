package com.example.greatchem.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.greatchem.R
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greatchem.databinding.FragmentMindMapBinding

import com.github.chrisbanes.photoview.PhotoView

class MindMapFragment : Fragment() {

    private var _binding: FragmentMindMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMindMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val mindMapImage: PhotoView = binding.mindMapImage
        mindMapImage.setImageResource(R.drawable.img_peta_konsep_2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}