import { API_BASE_URL } from "../util/constants.js";

const getApiKey = () => {
    const apiKey = sessionStorage.getItem('adminApiKey');
    if (!apiKey) {
        throw new Error('No API key found. Please login again.');
    }
    return apiKey;
};

const authenticatedFetch = async (url, options = {}) => {
    const apiKey = getApiKey();

    const headers = {
        'X-API-Key': apiKey,
        'Content-Type': 'application/json',
        ...options.headers,
    };

    const response = await fetch(url, {
        ...options,
        headers,
    });

    if (!response.ok) {
        if (response.status === 401) {
            throw new Error('Invalid API key. Please login again.');
        }
        const errorText = await response.text();
        throw new Error(`API error: ${response.status} - ${errorText}`);
    }

    return response;
};

export const getGatheringStatus = async () => {
    const response = await authenticatedFetch(`${API_BASE_URL}/gathering/status`);
    return await response.json();
};

export const startGathering = async (mode = 'RANDOM', batchSize = 10) => {
    const response = await authenticatedFetch(
        `${API_BASE_URL}/gathering/start?mode=${mode}&batchSize=${batchSize}`,
        { method: 'POST' }
    );
    return await response.json();
};

export const stopGathering = async () => {
    const response = await authenticatedFetch(
        `${API_BASE_URL}/gathering/stop`,
        { method: 'POST' }
    );
    return await response.json();
};
