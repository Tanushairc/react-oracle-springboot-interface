import React, { useState, useEffect } from 'react';

// Main React component for User Management
const UserManagementApp = () => {
  // State hooks to manage component data
  // useState returns [currentValue, setterFunction]
  
  // Array to store all users fetched from API
  const [users, setUsers] = useState([]);
  
  // Object to store form data for creating/editing users
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: ''
  });
  
  // Boolean to track if we're in edit mode or create mode
  const [isEditing, setIsEditing] = useState(false);
  
  // ID of user being edited (null when creating new user)
  const [editingId, setEditingId] = useState(null);
  
  // String to store search query
  const [searchTerm, setSearchTerm] = useState('');
  
  // Boolean to show loading state during API calls
  const [loading, setLoading] = useState(false);
  
  // String to display error messages
  const [error, setError] = useState('');
  
  // String to display success messages
  const [success, setSuccess] = useState('');

  // Base URL for API calls - matches Spring Boot server
  const API_BASE_URL = 'http://localhost:8080/api/users';

  // useEffect hook runs after component mounts
  // Empty dependency array [] means it runs only once
  useEffect(() => {
    fetchUsers();
  }, []);

  // Function to fetch all users from the API
  const fetchUsers = async () => {
    setLoading(true); // Show loading indicator
    setError(''); // Clear any previous errors
    
    try {
      // fetch() makes HTTP GET request to Spring Boot API
      const response = await fetch(API_BASE_URL);
      
      // Check if response is successful (status 200-299)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      // Convert response to JSON
      const data = await response.json();
      
      // Update users state with fetched data
      setUsers(data);
      
    } catch (err) {
      // Handle any errors during fetch
      setError('Failed to fetch users: ' + err.message);
      console.error('Error fetching users:', err);
    } finally {
      // Hide loading indicator regardless of success/failure
      setLoading(false);
    }
  };

  // Function to create a new user
  const createUser = async (userData) => {
    setLoading(true);
    setError('');
    
    try {
      // POST request to create new user
      const response = await fetch(API_BASE_URL, {
        method: 'POST', // HTTP method for creating resources
        headers: {
          'Content-Type': 'application/json', // Tell server we're sending JSON
        },
        body: JSON.stringify(userData), // Convert JS object to JSON string
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const newUser = await response.json();
      
      // Add new user to the users array
      setUsers(prevUsers => [...prevUsers, newUser]);
      
      setSuccess('User created successfully!');
      
      // Reset form
      resetForm();
      
    } catch (err) {
      setError('Failed to create user: ' + err.message);
      console.error('Error creating user:', err);
    } finally {
      setLoading(false);
    }
  };

  // Function to update an existing user
  const updateUser = async (id, userData) => {
    setLoading(true);
    setError('');
    
    try {
      // PUT request to update existing user
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'PUT', // HTTP method for updating resources
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const updatedUser = await response.json();
      
      // Update the user in the users array
      setUsers(prevUsers => 
        prevUsers.map(user => 
          user.id === id ? updatedUser : user
        )
      );
      
      setSuccess('User updated successfully!');
      resetForm();
      
    } catch (err) {
      setError('Failed to update user: ' + err.message);
      console.error('Error updating user:', err);
    } finally {
      setLoading(false);
    }
  };

  // Function to delete a user
  const deleteUser = async (id) => {
    // Confirm deletion with user
    if (!window.confirm('Are you sure you want to delete this user?')) {
      return;
    }
    
    setLoading(true);
    setError('');
    
    try {
      // DELETE request to remove user
      const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'DELETE', // HTTP method for deleting resources
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      // Remove user from the users array
      setUsers(prevUsers => prevUsers.filter(user => user.id !== id));
      
      setSuccess('User deleted successfully!');
      
    } catch (err) {
      setError('Failed to delete user: ' + err.message);
      console.error('Error deleting user:', err);
    } finally {
      setLoading(false);
    }
  };

  // Function to handle form submission
  const handleSubmit = (e) => {
    e.preventDefault(); // Prevent default form submission behavior
    
    // Basic validation
    if (!formData.name.trim() || !formData.email.trim()) {
      setError('Name and email are required');
      return;
    }
    
    // Email validation regex
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Please enter a valid email address');
      return;
    }
    
    // Call appropriate function based on mode
    if (isEditing) {
      updateUser(editingId, formData);
    } else {
      createUser(formData);
    }
  };

  // Function to handle input changes in the form
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    // Update formData state
    setFormData(prevData => ({
      ...prevData, // Spread operator to keep existing fields
      [name]: value // Update the specific field that changed
    }));
  };

  // Function to start editing a user
  const startEdit = (user) => {
    setFormData({
      name: user.name,
      email: user.email,
      phone: user.phone || ''
    });
    setIsEditing(true);
    setEditingId(user.id);
    setError('');
    setSuccess('');
  };

  // Function to reset form to initial state
  const resetForm = () => {
    setFormData({
      name: '',
      email: '',
      phone: ''
    });
    setIsEditing(false);
    setEditingId(null);
    setError('');
    setSuccess('');
  };

  // Function to search users
  const searchUsers = async () => {
    if (!searchTerm.trim()) {
      fetchUsers(); // If no search term, fetch all users
      return;
    }
    
    setLoading(true);
    setError('');
    
    try {
      // GET request with query parameter
      const response = await fetch(`${API_BASE_URL}/search?name=${encodeURIComponent(searchTerm)}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setUsers(data);
      
    } catch (err) {
      setError('Failed to search users: ' + err.message);
      console.error('Error searching users:', err);
    } finally {
      setLoading(false);
    }
  };

  // Function to clear search and show all users
  const clearSearch = () => {
    setSearchTerm('');
    fetchUsers();
  };

  // Clear messages after 5 seconds
  useEffect(() => {
    if (success || error) {
      const timer = setTimeout(() => {
        setSuccess('');
        setError('');
      }, 5000);
      
      // Cleanup function to clear timer if component unmounts
      return () => clearTimeout(timer);
    }
  }, [success, error]);

  // JSX - The UI structure
  return (
    <div className="min-h-screen bg-gray-100 py-8">
      <div className="max-w-6xl mx-auto px-4">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">User Management System</h1>
          <p className="text-gray-600">Manage users with full CRUD operations</p>
        </div>

        {/* Messages */}
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        
        {success && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            {success}
          </div>
        )}

        {/* Search Section */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-semibold mb-4">Search Users</h2>
          <div className="flex gap-4">
            <input
              type="text"
              placeholder="Search by name..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <button
              onClick={searchUsers}
              disabled={loading}
              className="px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50"
            >
              Search
            </button>
            <button
              onClick={clearSearch}
              className="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600"
            >
              Clear
            </button>
          </div>
        </div>

        {/* Form Section */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-semibold mb-4">
            {isEditing ? 'Edit User' : 'Add New User'}
          </h2>
          
          <div className="space-y-4">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
                Name *
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                Email *
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            
            <div>
              <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-1">
                Phone
              </label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleInputChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            
            <div className="flex gap-4">
              <button
                type="button"
                onClick={handleSubmit}
                disabled={loading}
                className="px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50"
              >
                {loading ? 'Processing...' : isEditing ? 'Update User' : 'Create User'}
              </button>
              
              {isEditing && (
                <button
                  type="button"
                  onClick={resetForm}
                  className="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600"
                >
                  Cancel
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Users List */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">Users ({users.length})</h2>
            <button
              onClick={fetchUsers}
              disabled={loading}
              className="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 disabled:opacity-50"
            >
              {loading ? 'Loading...' : 'Refresh'}
            </button>
          </div>
          
          {loading ? (
            <div className="text-center py-8">
              <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
              <p className="mt-2 text-gray-600">Loading users...</p>
            </div>
          ) : users.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              No users found
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full table-auto">
                <thead>
                  <tr className="bg-gray-50">
                    <th className="px-4 py-2 text-left">ID</th>
                    <th className="px-4 py-2 text-left">Name</th>
                    <th className="px-4 py-2 text-left">Email</th>
                    <th className="px-4 py-2 text-left">Phone</th>
                    <th className="px-4 py-2 text-left">Created At</th>
                    <th className="px-4 py-2 text-left">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id} className="border-t hover:bg-gray-50">
                      <td className="px-4 py-2">{user.id}</td>
                      <td className="px-4 py-2 font-medium">{user.name}</td>
                      <td className="px-4 py-2">{user.email}</td>
                      <td className="px-4 py-2">{user.phone || '-'}</td>
                      <td className="px-4 py-2">
                        {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-'}
                      </td>
                      <td className="px-4 py-2">
                        <div className="flex gap-2">
                          <button
                            onClick={() => startEdit(user)}
                            className="px-3 py-1 bg-yellow-500 text-white rounded hover:bg-yellow-600 text-sm"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => deleteUser(user.id)}
                            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-sm"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserManagementApp;