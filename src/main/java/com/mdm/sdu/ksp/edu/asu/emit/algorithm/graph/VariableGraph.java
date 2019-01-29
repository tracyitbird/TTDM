/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.mdm.sdu.ksp.edu.asu.emit.algorithm.graph;

import com.mdm.sdu.ksp.edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import com.mdm.sdu.ksp.edu.asu.emit.algorithm.graph.shortestpaths.DijkstraShortestPathAlg;
import com.mdm.sdu.ksp.edu.asu.emit.algorithm.utils.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;



/**
 * The class defines a graph which can be changed constantly.
 *  
 * @author yqi
 */
public class VariableGraph extends Graph {
	private Set<Long> remVertexIdSet = new HashSet<Long>();
	private Set<Pair<Long, Long>> remEdgeSet = new HashSet<Pair<Long, Long>>();

	/**
	 * Default constructor
	 */
	public VariableGraph() { }
	
	/**
	 * Constructor 1
	 * 
	 * @param dataFileName
	 */
	public VariableGraph(String dataFileName)	{
		super(dataFileName);
	}
	
	/**
	 * Constructor 2
	 * 
	 * @param graph
	 */
	public VariableGraph(Graph graph) {
		super(graph);
	}

	/**
	 * Set the set of vertices to be removed from the graph
	 * 
	 * @param remVertexList
	 */
	public void setDelVertexIdList(Collection<Long> remVertexList) {
		this.remVertexIdSet.addAll(remVertexList);
	}

	/**
	 * Set the set of edges to be removed from the graph
	 * 
	 * @param
	 */
	public void setDelEdgeHashcodeSet(Collection<Pair<Long, Long>> remEdgeCollection) {
		remEdgeSet.addAll(remEdgeCollection);
	}
	
	/**
	 * Add an edge to the set of removed edges
	 * 
	 * @param edge
	 */
	public void deleteEdge(Pair<Long, Long> edge) {
		remEdgeSet.add(edge);
	}
	
	/**
	 * Add a vertex to the set of removed vertices
	 * 
	 * @param vertexId
	 */
	public void deleteVertex(Long vertexId) {
		remVertexIdSet.add(vertexId);
	}
	
	public void recoverDeletedEdges() {
		remEdgeSet.clear();
	}

	public void recoverDeletedEdge(Pair<Long, Long> edge)	{
		remEdgeSet.remove(edge);
	}
	
	public void recoverDeletedVertices() {
		remVertexIdSet.clear();
	}
	
	public void recoverDeletedVertex(Long vertexId) {
		remVertexIdSet.remove(vertexId);
	}
	
	/**
	 * Return the weight associated with the input edge.
	 * 
	 * @param source
	 * @param sink
	 * @return
	 */
	public double getEdgeWeight(BaseVertex source, BaseVertex sink)	{
		if(source ==null || sink ==null){
			return  DISCONNECTED ;
		}
		long sourceId = source.getId();
		long sinkId = sink.getId();
		
		if (remVertexIdSet.contains(sourceId) || remVertexIdSet.contains(sinkId) ||
		   remEdgeSet.contains(new Pair<Long, Long>(sourceId, sinkId))) {
			return Graph.DISCONNECTED;
		}
		return super.getEdgeWeight(source, sink);
	}

	/**
	 * Return the weight associated with the input edge.
	 * 
	 * @param source
	 * @param sink
	 * @return
	 */
	public double getEdgeWeightOfGraph(BaseVertex source, BaseVertex sink) {
		return super.getEdgeWeight(source, sink);
	}
	
	/**
	 * Return the set of fan-outs of the input vertex.
	 * 
	 * @param vertex
	 * @return
	 */
	public Set<BaseVertex> getAdjacentVertices(BaseVertex vertex) {
		Set<BaseVertex> retSet = new HashSet<BaseVertex>();
		long startingVertexId = vertex.getId();
		if (!remVertexIdSet.contains(startingVertexId))	{
			Set<BaseVertex> adjVertexSet = super.getAdjacentVertices(vertex);
			for (BaseVertex curVertex : adjVertexSet) {
				long endingVertexId = curVertex.getId();
				if (remVertexIdSet.contains(endingVertexId) ||
					remEdgeSet.contains(new Pair<Long,Long>(startingVertexId, endingVertexId))) {
					continue;
				}
				// 
				retSet.add(curVertex);
			}
		}
		return retSet;
	}

	/**
	 * Get the set of vertices preceding the input vertex.
	 * 
	 * @param vertex
	 * @return
	 */
	public Set<BaseVertex> getPrecedentVertices(BaseVertex vertex) {
		Set<BaseVertex> retSet = new HashSet<BaseVertex>();
		if (!remVertexIdSet.contains(vertex.getId())) {
			long endingVertexId = vertex.getId();
			Set<BaseVertex> preVertexSet = super.getPrecedentVertices(vertex);
			for (BaseVertex curVertex : preVertexSet) {
				long startingVertexId = curVertex.getId();
				if (remVertexIdSet.contains(startingVertexId) ||
					remEdgeSet.contains(new Pair<Long, Long>(startingVertexId, endingVertexId))) {
					continue;
				}
				//
				retSet.add(curVertex);
			}
		}
		return retSet;
	}

	/**
	 * Get the list of vertices in the graph, except those removed.
	 * @return
	 */
	public List<BaseVertex> getVertexList() {
		List<BaseVertex> retList = new Vector<BaseVertex>();
		for (BaseVertex curVertex : super.getVertexList()) {
			if (remVertexIdSet.contains(curVertex.getId())) {
				continue;
			}
			retList.add(curVertex);
		}
		return retList;
	}

	/**
	 * Get the vertex corresponding to the input 'id', if exist. 
	 * 
	 * @param id
	 * @return
	 */
	public BaseVertex getVertex(int id)	{
		if (remVertexIdSet.contains(id)) {
			return null;
		} else {
			return super.getVertex(id);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to the class VariableGraph!");
		
		VariableGraph graph = new VariableGraph("data/test_50");
		graph.deleteVertex(13L);
		graph.deleteVertex(12L);
		graph.deleteVertex(10L);
		graph.deleteVertex(23L);
		graph.deleteVertex(47L);
		graph.deleteVertex(49L);
		graph.deleteVertex(3L);
		graph.deleteEdge(new Pair<Long, Long>(26l, 41l));
		DijkstraShortestPathAlg alg = new DijkstraShortestPathAlg(graph);
		System.out.println(alg.getShortestPath(graph.getVertex(0), graph.getVertex(20)));
	}
}
