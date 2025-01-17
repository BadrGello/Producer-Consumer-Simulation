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
          top: '-30px', // Position the count above the node
          left: '50%',
          transform: 'translateX(-50%)',
          fontSize: '12px', // Adjust font size
        }}
      >
        Count: {data.count}
     </div>
    } 

    {/* Time of service of products for the machines */}
    {data.serveTime !== undefined && 
      <div
        style={{
          position: 'absolute',
          top: '-30px', // Position the serveTime above the node
          left: '50%',
          transform: 'translateX(-50%)',
          fontSize: '12px', // Adjust font size
        }}
      >
        Time: {data.serveTime}
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
  // console.log(node)

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

  const [isDisabled, setIsDisabled] = useState(false); // Manage buttons status
  const [locked, setLocked] = useState(false); // Manage lock status of the Controls

  const handleInteractiveChange = (interactive) => {
    setLocked(!interactive);
  };

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
        data: { label: `M${machineCount}`, serveTime: 0, flashColor: '#ddd', flash: false }, // To test flash, set flash to true and flashColor to some color
        position: { x: Math.random() * 400, y: Math.random() * 400 },
      },
    ]);
    setMachineCount((prev) => prev + 1);
  };

  const resetNodes = () => {
    setNodes((nds) => 
      nds.map((node) => {
        if (node.id.startsWith('Q')) {
          // Reset count for Q nodes
          return {
            ...node,
            data: { ...node.data, count: 0 }
          };
        } else if (node.id.startsWith('M')) {
          // Reset serveTime for M nodes
          return {
            ...node,
            data: { ...node.data, serveTime: 0 }
          };
        }
        return node;
      })
    );
  };
  

  // Save the states for replay
  const [initialState, setInitialState] = useState({ nodes: [], edges: [] });
  const serializeGraph = () => {
    const graph = {};
  
    nodes.forEach((node) => {
      let targetQ = null;
      let connections = edges
        .filter((edge) => edge.source === node.id || edge.target === node.id)
        .map((edge) => (edge.source === node.id ? edge.target : edge.source));
  
      if (node.id.startsWith('M')) {
        
        edges.forEach((edge) => {
          if (node.id === edge.source) {
            targetQ = edge.target; // Capture the target Q
          }
        });
  
        if (targetQ) {
          // Remove targetQ first
          connections = connections.filter(conn => conn !== targetQ);
          // Append targetQ to the end
          connections.push(targetQ);
        }
      }
  
      graph[node.id] = connections;
    });
  
    return graph;
  };
  
  const startSimulation = async () => {
    setInitialState({ nodes, edges });
    resetNodes();

    const graph = serializeGraph();

    console.log('Serialized Graph:', graph);

    try {
      const response = await fetch('http://localhost:8080/run', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ myNetwork: graph }),
      });

      const result = await response.text();
      console.log(result);
    } catch (error) {
      console.error('Error starting simulation:', error);
      alert('Error starting simulation')
    }

    setIsDisabled(true);
    setLocked(true)
  };

  
  const replaySimulation = async () => {
   // stopSimulation();
    setNodes(initialState.nodes);
    setEdges(initialState.edges);

    resetNodes();
    
    try {
      const response = await fetch('http://localhost:8080/replay', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ }),
      });
      const result = await response.text();
      console.log(result);

    } catch (error) {
      console.error('Error in replay simulation:', error);
      alert('Error in replay simulation')
    }

    // Send to backend
    
    setIsDisabled(true);
    setLocked(true)
  };

  const stopSimulation = async () => {

    try {
      const response = await fetch('http://localhost:8080/stop', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ }),
      });

      const result = await response.text();
      console.log(result);
    } catch (error) {
      console.error('Error in stop simulation:', error);
      alert('Error in stop simulation')
    }

    setIsDisabled(false);
    setLocked(false)
  };
  
  const clearAll= async () =>  {

    setEdges([])
    setNodes([])
    setQueueCount(0)
    setMachineCount(0)

    try {
      const response = await fetch('http://localhost:8080/clear', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ }),
      });

      const result = await response.text();
      console.log(result);
    } catch (error) {
      console.error('Error in clear simulation:', error);
      alert('Error in clear simulation')
    }

    

    setIsDisabled(false);
    setLocked(false)
  };


  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080/ws');

    socket.onopen = () => {
      console.log('WebSocket connection established');
    };
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

      else if (data.type === 'machine-update') {
        setNodes((nds) =>
          nds.map((node) =>
            node.id === data.machineId
              ? { ...node, data: { ...node.data, serveTime: data.serveTime } }
              : node
          )
        );
      }
      
    };
    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    socket.onclose = (event) => {
      console.log('WebSocket connection closed:', event);
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

        // The following to handle toggle of Lock of the interactivity (shouldn't be able to move or interact with the nodes if a simulation is playing)
        interactionMode={locked ? 'none' : 'zoom'} // Lock/Unlock based on state
        nodesDraggable={!locked}
        nodesConnectable={!locked}
        elementsSelectable={!locked}
      >
        <Background />
        <Panel className="panel">
          <div>Toolbar:</div>

          <button onClick={addQueue} disabled={isDisabled}>Add Queue</button>
          <button onClick={addMachine} disabled={isDisabled}>Add Machine</button>
          <button className="simulation-button" onClick={startSimulation} disabled={isDisabled}>Start Simulation</button>
          <button className="simulation-button" onClick={replaySimulation} disabled={isDisabled}>Replay Previous Simulation</button>
          <button className="simulation-button" onClick={stopSimulation}>Stop Simulation</button>
          
          <button onClick={clearAll}>Clear All</button>
          <button onClick={shortcutsDisplay}>Shortcuts</button>
        </Panel>
        <Controls 
        
          showInteractive={true} 
          onInteractiveChange={(interactive) => handleInteractiveChange(interactive)}

        />
        <MiniMap />
      </ReactFlow>
    </div>
  );
};

export default App;
