import * as React from 'react';
import * as ReactDOM from 'react-dom';

interface HelloWorldProps {
    // Add any props you need here
}

class GatewayPage extends React.Component<HelloWorldProps, {}> {

    constructor(props: HelloWorldProps) {
        super(props);
    }

    render() {
        return (
            <div style={{
                padding: '20px',
                fontSize: '24px',
                fontWeight: 'bold',
                textAlign: 'center',
                color: '#333',
                borderRadius: '8px',
                margin: '20px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}>
                Hello World from Script Profiler!
            </div>
        );
    }
}

// Mount the component when the page loads
document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('react-app');
    if (container) {
        ReactDOM.render(<HelloWorld />, container);
    }
});

export default HelloWorld;