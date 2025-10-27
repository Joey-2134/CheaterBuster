import {API_BASE_URL} from "../util/constants.js";

export const getPlayerCount = async () => {
    const response = await fetch(`${API_BASE_URL}/api/gathering/count`, {
        headers: {
            'X-API-Key': import.meta.env.VITE_API_KEY
        }
    });
    if (!response.ok) throw new Error(`API error: ${response.status}`);
    return await response.json();
};

