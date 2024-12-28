import React, { useState, useCallback, useEffect, useMemo } from 'react';
import ReactFlow, {
  addEdge,
  Background,
  Controls,
  MiniMap,
  useEdgesState,
  useNodesState,
  Panel, 
  Handle,
} from 'reactflow';
import 'reactflow/dist/style.css';

const CustomNode = ({ data }) => (
  <div style={customNodeStyles(data)}>
    {/* Handle is the points that connect nodes together */}
    <Handle
      type="source"
      position="left"
      style={{ background: '#555' }}
    />

    <div>{data.label}</div>

    {/* Count of products for the queues */}
    {data.count !== undefined && 
      <div
        style={{
          position: 'absolute',
          top: '-20px', // Position the count above the node
          left: '50%',
          transform: 'translateX(-50%)',
          fontSize: '12px', // Adjust font size
        }}
      >
        Count: {data.count}
     </div>
    } 

    <Handle
      type="target"
      position="right"
      style={{ background: '#555' }}
    />
  </div>
);

const customNodeStyles = (node) => {
  const nodeType = node.label ? node.label[0] : ''; // nodeType is either M or Q
  const nodeFlash = node.flash ? node.flashColor : '#ddd';
  console.log(node)

  return {
    background: nodeFlash,
    border: '1px solid #333',
    padding: '10px',
    borderRadius: nodeType === 'M'  ? '50%' : '5px', // Round for Machines, Rectangular for Queues
    width: nodeType === 'M'  ? '60px' : '90px',    // Different sizes for better visual distinction
    height: nodeType === 'M'  ? '60px' : 'auto',   // Ensure machines are circles
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    textAlign: 'center',
    position: 'relative',
  }
  
};

const nodeTypes = { customNode: CustomNode };

const App = () => {
  // onNodesChange and onEdgesChange are special handlers from reactFlow that automatically handles any change of the state of the edges/nodes
  // ex: changing their position with dragging, deleting by using Backspace, etc...
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [queueCount, setQueueCount] = useState(0);
  const [machineCount, setMachineCount] = useState(0);

  const shortcutsDisplay = () => {
    alert("Select a node/edge and click \"Backspace\" to delete.\nTo select multiple objects: \"Shift\" + drag")
  };

  const onConnect = useCallback(
    (connection) => setEdges((eds) => addEdge(connection, eds)),
    [setEdges]
  );

  const addQueue = () => {
    setNodes((nds) => [
      ...nds,
      {
        id: `Q${queueCount}`,
        type: 'customNode',
        data: { label: `Q${queueCount}`, count: 0 }, // Count is the number of products in this queue 
        position: { x: Math.random() * 400, y: Math.random() * 400 },
      },
    ]);
    setQueueCount((prev) => prev + 1);
  };

  const addMachine = () => {
    setNodes((nds) => [
      ...nds,
      {
        id: `M${machineCount}`,
        type: 'customNode',
        data: { label: `M${machineCount}`, flashColor: '#ddd', flash: false }, // To test flash, set flash to true and flashColor to some color
        position: { x: Math.random() * 400, y: Math.random() * 400 },
      },
    ]);
    setMachineCount((prev) => prev + 1);
  };

  // Save the states for replay
  const [initialState, setInitialState] = useState({ nodes: [], edges: [] });

  const serializeGraph = () => {

    const graph = {
      nodes: nodes.map((node) => ({
        id: node.id,
        // type: node.type,
      })),
      edges: edges.map((edge) => ({
        source: edge.source,
        target: edge.target,
      })),
    };

    return graph

  };

  const startSimulation = () => {

    // Save the states for the replay option
    setInitialState({ nodes, edges });

    const graph = serializeGraph()

    // Send to backend
    console.log('Serialized Graph:', graph); 

  };

  const replaySimulation = () => {

    
    setNodes(initialState.nodes);
    setEdges(initialState.edges);

    // Send to backend
    console.log('Replay Simulation');

  };

  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080');
    socket.onmessage = (message) => {
      const data = JSON.parse(message.data);
      console.log("Data Websocket, ", data)

      // Data examples
      // {
      //   "type": "machine-flash",
      //   "machineId": "M1",
      //   "flashColor": "#ff0" 
      // }

      // {
      //   "type": "queue-update",
      //   "queueId": "Q1",
      //   "count": "20" 
      // }

      if (data.type === 'machine-flash') {
        setNodes((nds) =>
          nds.map((node) =>
            node.id === data.machineId
              ? { ...node, data: { ...node.data, flashColor: data.flashColor || '#ddd', flash: true } }
              : node
          )
        );

        setTimeout(() => {
          setNodes((nds) =>
            nds.map((node) =>
              node.id === data.machineId
                ? { ...node, data: { ...node.data, flashColor: '#ddd', flash: false } }
                : node
            )
          );
        }, 1000);
      } 

      else if (data.type === 'queue-update') {
        setNodes((nds) =>
          nds.map((node) =>
            node.id === data.queueId
              ? { ...node, data: { ...node.data, count: data.count } }
              : node
          )
        );
      }
    };

    return () => socket.close();
  }, []);
  
  return (
    <div style={{ height: '90vh', border: '1px solid #ddd' }}>
      

      <ReactFlow
        nodeTypes={nodeTypes}
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        fitView
      >
        <Background />
        <Panel className="panel">
          <div>Toolbar:</div>

          <button onClick={addQueue}>Add Queue</button>
          <button onClick={addMachine}>Add Machine</button>
          <button className="simulation-button" onClick={startSimulation}>Start Simulation</button>
          <button className="simulation-button" onClick={replaySimulation}>Replay Previous Simulation</button>

          <button onClick={shortcutsDisplay}>Shortcuts</button>
        </Panel>
        <Controls />
        <MiniMap />
      </ReactFlow>
    </div>
  );
};

export default App;
